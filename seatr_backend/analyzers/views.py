from rest_framework.views    import APIView
from rest_framework.response import Response
from rest_framework          import status, exceptions
from collections             import defaultdict

from questions.models import Questions, QuestionsStudentsMap
from kcs.models       import KCsQuestionsMap
from students.models  import Students

import json


class Analyzer1(APIView):
    ATTEMPTED = 1
    SEEN      = 2
    CORRECT   = 3
    INCORRECT = 4

    def post(self, request, format=None):
        # get the post data: studentId and questionIds of the candidate questions given by OPE
        questionIds = json.loads(request.POST.get('questionIds'))
        studentId   = int(request.POST.get('studentId', None))

        # check if the student exists in SEATR
        try:
            Students.objects.get(id=studentId)
        except Students.DoesNotExist:
            print("student with id=" + str(studentId) + " not present in SEATR")
            # raise exceptions.NotFound(detail="student with id=" + str(studentId) + " not present in SEATR")

        # if studentId not present try to create the Students object if external_id given
        try:
            externalId = int(request.POST.get('externalId', None))
            Students.objects.create(external_id=externalId, id=studentId)
        except TypeError as e:
            raise TypeError("externalId can't be None") from e
        except ValueError as e:
            raise ValueError("externalId can't be a string") from e
        # possible bug: no exception raised if model creation fails

        # get all the questions solved/seen by the student and get the KCs of those questions
        # ie. get all the KCs that the student has seen/mastered
        questionsSolved = QuestionsStudentsMap.objects.filter(student__in=[studentId])
        # if student hasn't solved any question, return half of the questions from the request body
        if len(questionsSolved) == 0:
            return Response({
                "questions": questionIds[0:len(questionIds)//2 + 1]
            })
        # filter out the questionIds from the questionsSolved list
        questionSolvedIds = [x["question"] for x in questionsSolved]
        # get the kcs of the solved questions
        kcQuestionsSolvedQuestions = KCsQuestionsMap.objects.filter(question__in=questionSolvedIds)
        # the following dict stores {question} = [kc1, kc2, ...]
        solvedQuestionsKcMap = defaultdict(set)
        for x in kcQuestionsSolvedQuestions:
            kc       = x["kc"]
            question = x["question"]
            solvedQuestionsKcMap[question].add(kc)

        # find the student expertise in the KCs of the questions that student has solved/seen
        # kcScoreMap stores the scores of the {kc} = score
        kcScoreMap = defaultdict(lambda: defaultdict(int))
        for x in questionsSolved:
            expertise  = x["status"]
            questionId = x["question"]
            # for all the kcs associated with this question give it a score
            for kc in questionKcMap[questionId]:
                if expertise == self.ATTEMPTED:
                    kcScoreMap[kc][self.ATTEMPTED] += 1
                elif expertise == self.SEEN:
                    kcScoreMap[kc][self.SEEN] += 1
                elif expertise == self.CORRECT:
                    kcScoreMap[kc][self.CORRECT] += 1
                else:
                    kcScoreMap[kc][self.INCORRECT] += 1

        # get all the KCs of the questions sent by OPE ie. present as the request body
        kcQuestionsOPE = KCsQuestionsMap.objects.filter(question__in=questionIds)

        # for all the questions sent by OPE find the kc status using the question status
        finalKcScore = defaultdict()
        for x in kcQuestionsOPE:
            kc       = x["kc"]
            question = x["question"]
            # kc never seen by student
            if kc not in kcScoreMap:
                finalKcScore[kc] = 5
            # at least 1 studied but not in solved
            elif kcScoreMap[kc][self.SEEN] >= 1 and kcScoreMap[kc][self.SOLVED] == 0:
                finalKcScore[kc] = max(finalKcScore[kc], 4)
            # at least 1 incorrect, but never solved correctly
            elif kcScoreMap[kc][self.INCORRECT] >= 1 and kcScoreMap[kc][self.CORRECT] == 0:
                finalKcScore[kc] = max(finalKcScore[kc], 3)
            # only 1 correct
            elif kcScoreMap[kc][self.CORRECT] == 1:
                finalKcScore[kc] = max(finalKcScore[kc], 2)
            # correct 2 times
            elif kcScoreMap[kc][self.CORRECT] == 2:
                finalKcScore[kc] = max(finalKcScore[kc], 1)
            # correct 3 or more time
            elif kc in kcScoreMap[kc][self.CORRECT] and kcScoreMap[kc][self.CORRECT] >= 3:
                finalKcScore[kc] = max(finalKcScore[kc], 0)

        # for all the kcs involved in the recommended questions (by OPE) find the students expertise
        questionPriorityMap   = defaultdict(int)
        questionComplexityMap = defaultdict(int)
        for x in kcQuestionsOPE:
            question = x["question"]
            kc       = x["kc"]
            questionPriorityMap[question]    = max(questionPriorityMap[question], finalKcScore[kc])
            questionComplexityMap[question] += finalKcScore[kc]

        # do the actual recommendation ie. maximum priority and minimal complexity
        # get the questions which have maximum priority
        maxiVal = 0
        priorityQuestions = []
        for k, v in questionPriorityMap:
            if v == maxiVal:
                priorityQuestions.append(k)
            elif v > maxiVal:
                maxiVal = v
                priorityQuestions = []
                priorityQuestions.append(k)

        # now get the questions with minimal complexity from the above
        finalQuestions = []
        for question in priorityQuestions:
            finalQuestions.append((questionComplexityMap[question], question))
        finalQuestions.sort()
        answer = [x[1] for x in finalQuestions]
        return Response({
            "questions": answer
        }, status=status.HTTP_200_OK)
