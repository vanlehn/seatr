from rest_framework.views    import APIView
from rest_framework.response import Response
from rest_framework          import status, exceptions
from collections             import defaultdict

from questions.models import Questions, QuestionsUserMap, CategoryUserMap, QuestionAttempts, KCsQuestionsMap
from users.models  import User
from courses.models   import Courses, CoursesUserMap


class AnalyzerSimple(APIView):
    UNSTUDIED = 0
    STUDIED   = 1
    CORRECT   = 2
    INCORRECT = 3

    LOCKED     = 0
    UNLOCKED   = 1
    FAMILIAR   = 2
    UNFAMILIAR = 3

    def get(self, request):
        # get the GET data: studentId and courseId given by OPE
        # numbTasks: number of questions seatr should recommend
        try:
            studentId   = int(request.query_params.get('external_student_id', None))
            courseId    = int(request.query_params.get('external_course_id',  None))
            # numbTasks   = int(request.query_params.get('number_of_tasks',     None))
        except TypeError as e:
            raise TypeError("get parameters can't be None") from e
        except ValueError as e:
            raise ValueError("the data type of GET parameters are incorrect") from e

        # check if course with courseId exists
        try:
            Courses.objects.get(external_id=courseId)
        except Courses.DoesNotExist:
            raise exceptions.NotFound(detail="course with external_course_id=" + str(courseId) + " not present in SEATR")

        # check if the student exists in SEATR
        try:
            User.objects.get(external_id=studentId)
        except User.DoesNotExist:
            print("student with id=" + str(studentId) + " not present in SEATR creating one now")
            try:
                # create the student
                User.objects.create(external_id=studentId)
            except TypeError as e:
                raise TypeError("external_student_id can't be None") from e
            except ValueError as e:
                raise ValueError("external_student_id must be an integer") from e
            # raise exceptions.NotFound(detail="student with id=" + str(studentId) + " not present in SEATR")
            try:
                # create entry in CoursesStudentMap for the new studentId
                course  = Courses.objects.get(external_id=courseId)
                student = User.objects.get(external_id=studentId)
                CoursesStudentsMap.objects.create(course=course, student=student)
            # no need to check for valid external_student_id as its created above
            except TypeError as e:
                raise TypeError("external_course_id can't be None") from e
            except ValueError as e:
                raise ValueError("external_course_id can't be a string") from e
        
        # get all the familiar subcategories
        validCategories = CategoryStudentMap.objects.filter(student=student, course=course, status=FAMILIAR)

        # get the recent Questions
        recentAttempts = QuestionAttempts.objects.all()
        if recentAttempts >= 8:
            recentAttempts = recentAttempts[-7]

        # get all the questions from the subcategories minus the recent attempts
        possibleQuestions = validCategories.questions.exclude(recentAttempts)

        # 1. find the kcs of all the possibleQuestions
        kcsPossibleQuestions =  KCsQuestionsMap.objects.filter(question__in=possibleQuestions)
        
        # 2. find the priority of the kcs of the user
        kcsStudent = KCsStudentsMap.objects.filter(student=studentId)
        studentKcPriorityMap = defaultdict(int)
        for x in kcsStudent:
            studentKcPriorityMap[x["kc"]] = x["priority"]


        # 3. for each kcsPossibleQuestion in kcsPossibleQuestions, find priority of the question
        # kcScoreMap stores the scores of the {kc} = score
        questionPriorityMap   = defaultdict(lambda: defaultdict(int))
        questionComplexityMap = defaultdict(lambda: defaultdict(int))
        for x in kcsPossibleQuestions:
            kc = x["kc"] 
            questionPriorityMap[x["question"]]    = max(questionPriorityMap[x["question"]], studentKcPriorityMap[kc])
            questionComplexityMap[x["question"]] += studentKcPriorityMap[kc]

        # do the actual recommendation ie. maximum priority and minimal complexity
        # get the questions which have maximum priority
        maxiVal = 0
        priorityQuestions = []
        for k, v in questionPriorityMap.items():
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

        # 1. get the kcs for the final questions
        # 2. sort them based on importance
        if len(answer) > 5:
            kcs           = KCsQuestionsMap.objects.filter(question__in=answer)
            ##### TODO move this to mongo
            importanceKcs = importanceKcs.objects.filter(kc__in=kcs)
            answer        = [(x["importance"], x["kc"], KCsQuestionsMap[x["kc"]]) for x in importanceKcs]
            answer.sort()
            answer = [x[2] for x in answer]
            answer = answer[:5]

        return Response({
            "questions": answer
        }, status=status.HTTP_200_OK)













        # get all the questions solved/seen by the student and get the KCs of those questions
        # ie. get all the KCs that the student has seen/mastered
        student         = Students.objects.get(external_id=studentId)
        questionsSolved = QuestionsStudentsMap.objects.filter(student__in=[student.id])
        # if student hasn't solved any question, return half of the questions from the request body
        if len(questionsSolved) == 0:
            return Response({
                "questions": questionIds[0:min(len(questionIds)//2 + 1, numbTasks)]
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
            elif kcScoreMap[kc][self.ATTEMPTED] >= 1 and kcScoreMap[kc][self.SOLVED] == 0:
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
