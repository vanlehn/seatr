from rest_framework.views    import APIView
from rest_framework.response import Response
from rest_framework          import status, exceptions
from collections             import defaultdict

from questions.models import *
from users.models  import User
from courses.models   import Courses, CoursesUserMap

STUDIED   = 0
CORRECT   = 1
INCORRECT = 2

LOCKED     = 0
UNLOCKED   = 1
FAMILIAR   = 2
UNFAMILIAR = 3

class AnalyzerSimple(APIView):

    def get(self, request):
        # get the GET data: studentId and courseId given by OPE
        try:
            # studentId   = int(request.user.external_id)
            userId   = int(request.query_params.get('external_user_id',  None))
            courseId = int(request.query_params.get('external_course_id',  None))
        except TypeError as e:
            raise TypeError("external_course_id can't be None") from e
        except ValueError as e:
            raise ValueError("the data type of external_course_id is incorrect") from e

        # check if course with courseId exists
        try:
            Courses.objects.get(external_id=courseId)
        except Courses.DoesNotExist:
            raise exceptions.NotFound(detail="course with external_course_id=" + str(courseId) + " not present in SEATR")
        
        # get all the familiar subcategories
        validCategoryIds = [x[0] for x in CategoryUserMap.objects.filter(user_id=userId, status=FAMILIAR).values_list("category_id")]

        # find the edge sub-category
        validCategoryIds.sort()
        if len(validCategoryIds) > 0:
            try:
                i = 1
                # subCategory may be category, in that case fetch the next Category 
                subCategory = Category.objects.get(external_id=validCategoryIds[-1] + i)
                while subCategory.parent_id == -1:
                    i += 1
                    subCategory = Category.objects.get(external_id=validCategoryIds[-1] + i)
            except:
                # all Categories have been exhausted and still no sub-category found
                subCategory = None

        # mark the first sub-category as familiar 
        else:
            i = 1
            subCategory = Category.objects.get(external_id=i)
            while subCategory.parent_id == -1:
                subCategory.status = UNLOCKED
                i += 1
                subCategory = Category.objects.get(external_id=i)
        
        if subCategory:
            subCategory.status = FAMILIAR
            validCategoryIds.append(subCategory.external_id)
        
        print(validCategoryIds)

        # get the recent Questions
        recentAttempts = [x[0] for x in QuestionAttempts.objects.all().order_by("id").values_list("question_id")]
        if len(recentAttempts) >= 8:
            recentAttempts = recentAttempts[-7:]

        # get all the questions from the subcategories minus the recent attempts
        possibleQuestionIds = QuestionsCategoryCourseMap.objects.filter(category_id__in=validCategoryIds).exclude(question_id__in=recentAttempts).values_list("question_id")

        # 1. find the kcs of all the possibleQuestionIds
        kcsPossibleQuestions =  KCsQuestionsMap.objects.filter(question_id__in=possibleQuestionIds)
        
        # 2. find the priority of the kcs of the user
        kcsStudent = KCsUserMap.objects.filter(user_id=userId)
        studentKcPriorityMap = defaultdict(int)
        for x in kcsStudent:
            studentKcPriorityMap[x.kc_id] = x.priority


        # 3. for each kcsPossibleQuestion in kcsPossibleQuestions, find priority of the question
        # kcScoreMap stores the scores of the {kc} = score
        questionPriorityMap   = defaultdict(int)
        questionComplexityMap = defaultdict(int)
        for x in kcsPossibleQuestions:
            kc = x.kc
            questionPriorityMap[x.question_id]    = max(questionPriorityMap[x.question_id], studentKcPriorityMap[kc])
            questionComplexityMap[x.question_id] += studentKcPriorityMap[kc]

        if len(questionPriorityMap.keys()) <= 5:
            return Response({
                "questions": questionPriorityMap.keys()
            }, status=status.HTTP_200_OK)

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

        if len(priorityQuestions) <= 5:
            return Response({
                "questions": priorityQuestions
            }, status=status.HTTP_200_OK)

        # now get the questions with minimal complexity from the above
        finalQuestions = []
        for question in priorityQuestions:
            finalQuestions.append((questionComplexityMap[question], question))
        finalQuestions.sort()
        answer = [x[1] for x in finalQuestions]

        # 1. get the kcs for the final questions
        # 2. sort them based on importance
        if len(answer) > 5:
            kcImportanceMap = {}
            importanceKcs   = KCs.objects.all()
            for kc in importanceKcs:
                kcImportanceMap[kc.external_id] = kc.importance
                
            kcs    = KCsQuestionsMap.objects.filter(question__in=answer)
            answer = []
            for kc in kcs:
                answer.append((kcImportanceMap[kc.kc_id], kc.question_id))
            answer.sort()
            answer = [x[1] for x in answer]
            answer = answer[:5]

        return Response({
            "questions": answer
        }, status=status.HTTP_200_OK)