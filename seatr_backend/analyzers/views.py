from rest_framework.views    import APIView
from rest_framework.response import Response
from rest_framework          import status, exceptions
from collections             import defaultdict

from questions.models import *
from users.models     import User
from courses.models   import Courses, CoursesUserMap

STUDIED   = 0
CORRECT   = 1
INCORRECT = 2

LOCKED     = 0
UNLOCKED   = 1
FAMILIAR   = 2
UNFAMILIAR = 3
EDGE       = 4

class AnalyzerSimple(APIView):

    def getEdgeCategory(self, userId, familiarSubCategoryIds):
        # edge subcategory exists
        try:
            edgeCategory = CategoryUserMap.objects.get(user_id=userId, status=EDGE)
        # edge subcategory doesn't exist
        except:
            edgeCategory        = None
            categories          = Category.objects.all().order_by("external_id")
            categoryUserMaps    = CategoryUserMap.objects.filter(user_id=userId)
            categoryIdStatusMap = {}
            for categoryUserMap in categoryUserMaps:
                categoryIdStatusMap[categoryUserMap.category_id] = categoryUserMap.status

            for category in categories:
                if category.parent_id == -1 or (category.external_id in categoryIdStatusMap and categoryIdStatusMap[category.external_id] == FAMILIAR):
                    continue 

                categoryUserMap        = CategoryUserMap.objects.get(category=category, user_id=userId)
                categoryUserMap.status = EDGE
                edgeCategory           = categoryUserMap
                categoryUserMap.save(update_fields=['status'])
                break
        finally:
            if edgeCategory is not None:
                print("edgeCategory", edgeCategory.category_id)
                familiarSubCategoryIds.append(edgeCategory.category_id)

        #         i = 1
        #         while edgeCategory is None or edgeCategory.parent_id == -1:
        #             if len(familiarSubCategoryIds) == 0:
        #                 edgeCategory = Category.objects.get(external_id=i)
        #             else:
        #                 edgeCategory = Category.objects.get(external_id=familiarSubCategoryIds[-1] + i)
        #             i += 1
        #         edgeCategory = CategoryUserMap(status=EDGE, user_id=userId, category=edgeCategory)
        #         edgeCategory.save()
        #     except:
        #         edgeCategory = None
        # finally:
        #     if edgeCategory is not None:
        #         print("edgeCategory", edgeCategory.category_id)
        #         familiarSubCategoryIds.append(edgeCategory.category_id)


    def get(self, request):
        # get the GET data: studentId and courseId given by OPE
        try:
            # studentId   = int(request.user.external_id)
            userId   = int(request.query_params.get('external_user_id',  None))
            courseId = int(request.query_params.get('external_course_id',  None))
        except TypeError as e:
            raise TypeError("external_course_id and external_user_id can't be None") from e
        except ValueError as e:
            raise ValueError("the data type of external_course_id or external_user_id is incorrect") from e

        # check if course with courseId exists
        try:
            Courses.objects.get(external_id=courseId)
        except Courses.DoesNotExist:
            raise exceptions.NotFound(detail="course with external_course_id=" + str(courseId) + " not present in SEATR")

        # check if the user with userId exists
        try:
            User.objects.get(external_id=userId)
        except User.DoesNotExist:
            raise exceptions.NotFound(detail="user with external_user_id=" + str(userId) + " not present in SEATR")

        # get all the familiar subcategories
        familiarSubCategoryIds = sorted([x[0] for x in CategoryUserMap.objects.filter(user_id=userId, status=FAMILIAR).values_list("category_id")])
        print("familiarSubCategoryIds", familiarSubCategoryIds)
        # find the edge sub-category
        self.getEdgeCategory(userId, familiarSubCategoryIds)
        print("familiarSubCategoryIds + edgeSubCategories", familiarSubCategoryIds)
                
        # get the recent Questions
        ##### TODO: do pagination here
        recentAttempts = [x[0] for x in QuestionAttempts.objects.filter(user_id=userId).order_by("id").values_list("question_id")]
        if len(recentAttempts) >= 8:
            recentAttempts = recentAttempts[-7:]
        
        print("recentAttempts")
        print(recentAttempts)

        # get all the questions from the subcategories minus the recent attempts
        possibleQuestionIds = [x[0] for x in QuestionsCategoryCourseMap.objects.filter(category_id__in=familiarSubCategoryIds).exclude(question_id__in=recentAttempts).values_list("question_id")]

        print("possibleQuestionIds", possibleQuestionIds)

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
            kcId = x.kc_id
            questionPriorityMap[x.question_id]    = max(questionPriorityMap[x.question_id], studentKcPriorityMap[kcId])
            questionComplexityMap[x.question_id] += studentKcPriorityMap[kcId]

        print("questionPriorityMap", questionPriorityMap)
        print("questionComplexityMap", questionComplexityMap)

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

        print("priorityQuestions", priorityQuestions)

        if len(priorityQuestions) <= 5:
            return Response({
                "questions": priorityQuestions
            }, status=status.HTTP_200_OK)

        # now get the questions with minimal complexity from the above
        finalQuestions = []
        for questionId in priorityQuestions:
            finalQuestions.append((questionComplexityMap[questionId], questionId))
        finalQuestions.sort()
        answer = [x[1] for x in finalQuestions]

        print("complex-answer", answer)

        # 1. get the kcs for the final questions
        # 2. sort them based on importance
        if len(answer) > 5:
            # map the questions with the kcs
            kcQuestionMapper = defaultdict(list)
            kcs              = []
            kCsQuestionsMaps = KCsQuestionsMap.objects.filter(question_id__in=answer)
            for kCsQuestionsMap in kCsQuestionsMaps:
                kcQuestionMapper[kCsQuestionsMap.question_id].append(kCsQuestionsMap.kc_id)
                kcs.append(kCsQuestionsMap.kc_id)
            kcs = list(set(kcs))

            # map the kcs and their importances
            kcs             = KCs.objects.filter(external_id__in=kcs)
            kcImportanceMap = {}
            for kc in kcs:
                kcImportanceMap[kc.external_id] = kc.importance
            
            # find the importance of questions based on the importance of the involved kcs
            questionImportanceMap = defaultdict(int)
            for questionId, kcIds in kcQuestionMapper.items():
                for kcId in kcIds:
                    questionImportanceMap[questionId] = max(questionImportanceMap[questionId], kcImportanceMap[kcId])
                
            answer = []
            for questionId, importance in questionImportanceMap.items():
                answer.append((importance, questionId))
            answer.sort(reverse=True)
            answer = [x[1] for x in answer]
            answer = answer[:5]
        print("answer", answer)
        return Response({
            "questions": answer
        }, status=status.HTTP_200_OK)