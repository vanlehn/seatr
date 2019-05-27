from rest_framework                  import generics, status, exceptions
from rest_framework.views            import APIView
from rest_framework.response         import Response

# import the models
from questions.models import *
from users.models     import User

# import the serializers
from questions.serializers import QuestionsSerializer, KCsSerializer

from collections import defaultdict

STUDIED    = 0
CORRECT    = 1
INCORRECT  = 2
    
LOCKED     = 0
UNLOCKED   = 1
FAMILIAR   = 2
UNFAMILIAR = 3

class ListCreateQuestions(generics.ListCreateAPIView):
    queryset         = Questions.objects.all()
    serializer_class = QuestionsSerializer
    pagination_class = None


class ListCreateKCs(generics.ListCreateAPIView):
    queryset         = KCs.objects.all()
    serializer_class = KCsSerializer
    pagination_class = None


class UpdateCreateStatus(APIView):
    # populates/updates the QuestionsUserMap table with the given Question and User (Student)
    # if entry doesn't exist create an entry and update the status
    # if entry already exists and if the question hasn't been answered correctly, update the status
    def post(self, request, format=None):
        try:
            questionExternalId = int(request.data.get('external_task_id'))
            studentExternalId  = int(request.data.get('external_student_id'))
            _status            = request.data.get('status')
            if _status == "studied":
                _status = STUDIED
            elif _status == "correct":
                _status = CORRECT
            else:
                _status = INCORRECT
        except TypeError as e:
            raise TypeError("POST parameters can't be None") from e
        except ValueError as e:
            raise ValueError("the data type of POST parameters are incorrect") from e

        # get the student.id, question.id, from the externalIds
        try:
            question = Questions.objects.get(external_id=questionExternalId)
        except Questions.DoesNotExist:
            raise exceptions.NotFound("question_external_id " + str(questionExternalId) + " is invalid")
        try:
            student = User.objects.get(external_id=studentExternalId)
        except User.DoesNotExist:
            raise exceptions.NotFound("student_external_id " + str(studentExternalId) + " is invalid")

        questionStudentMap = QuestionsUserMap.objects.get_or_create(student=student, question=question)
        created = True if questionStudentMap[1] is True else False
        if created is True or questionStudentMap[0].status is not CORRECT:
            questionStudentMap[0].status = _status
            questionStudentMap[0].save()

        return Response({
            "text": "entry created"
        }, status=status.HTTP_201_CREATED)


class MarkQuestionInteraction(APIView):
    # this function is called whenever a student interacts ie. attempts to answer or studies a question
    # it does the following:
    # 1. unlocking categories, marking subcategories familiar: when student interacts with a question, the status might change ie. unstudied -> correct, incorrect -> correct etc depending upon the interaction, new categories might be unlocked and subcategories might change to familiar
    def post(self, request, format=None):
        print("*****", request.data)

        userId     = int(request.data.get('external_student_id', None))
        _status    = int(request.data.get('status', None))
        courseId   = int(request.data.get('external_course_id',  None))
        questionId = int(request.data.get('external_task_id', None))

        if _status not in [STUDIED, CORRECT, INCORRECT]:
            return Response({
                "msg": "something is wrong, the question status can only be STUDIED, CORRECT, INCORRECT but its" + " " + _status 
            }, status=status.HTTP_417_EXPECTATION_FAILED)
        
        # mark that the question has been attempted
        QuestionAttempts.objects.create(question_id=questionId, user_id=userId)
        
        # canChange answers the question-- Can this interaction potentially "unlock" a new category or make a subcategory "familiar"?
        # this can only happen if the question is a new question ie. is being stdudied or attempted (either correctly or incorrectly) the first time
        canChange = False
        # see if the student has studied/attempted this question before
        try:
            questionsUserMap = QuestionsUserMap.objects.get(user_id=userId, course_id=courseId, question_id=questionId)
            currentStatus    = questionsUserMap.status
        except:
            currentStatus    = None
        finally:
            if currentStatus is None:
                canChange = True
        
        # if the currentStatus == CORRECT, don't do anything, its a dead state
        # if the currentStatus == INCORRECT, 
        #   if _status is CORRECT/STUDIED, save the new status, 
        #   if _status is INCORRECT, nothing changed so return 
        # if the currentStatus == STUDIED,
        #   if _status is STUDIED, nothing changed don't do anything
        #   if _status is CORRECT/INCORRECT, save the new status
        if currentStatus is not None:
            # if student has already "correctly" solved the question don't do anything
            if currentStatus == CORRECT:
                return Response({
                    "msg": "question has already been answered correctly" 
                }, status=status.HTTP_200_OK)
            
            # if question is "incorrect" it can move only to "studied" and "correct"
            elif currentStatus == INCORRECT:
                if _status == INCORRECT:
                    return Response({
                        "msg": "the student made a repeated incorrect attempt" 
                    }, status=status.HTTP_200_OK)
            # if question has been "studied", it can move to "correct" and "incorrect"
            else:
                if _status == STUDIED:
                    return Response({
                        "msg": "the student studied the question again" 
                    }, status=status.HTTP_200_OK)

        if currentStatus is None:
            questionsUserMap = QuestionsUserMap(user_id=userId, course_id=courseId, question_id=questionId)
        questionsUserMap.status = _status
        questionsUserMap.save()
        
        if canChange is False:
            return Response({
                "msg": "the question:" +  str(questionId) + " " + "was previously studied or attempted (correctly/incorrectly)"
            }, status=status.HTTP_201_CREATED)

        
        # check if due to the new interaction any category gets unlocked
        # get the sub-category and category of the question
        questionsCategoryCourseMap = QuestionsCategoryCourseMap.objects.get(question_id=questionId, course_id=courseId)
        categoryId                 = questionsCategoryCourseMap.category.parent_id

        print("categoryId", categoryId)
        print("\n")

        # see if the new interaction changes the status of the category and subcategory
        # get all questions solved by user in that category
        # find all the subCategories of the category 
        subCategoryIds  = [x[0] for x in Category.objects.filter(parent_id=categoryId).values_list("external_id")]

        print("subCategoryIds", subCategoryIds)
        print("\n")

        # get all the questions in the subcategories
        subCategoryQuestionIds = set([x[0] for x in QuestionsCategoryCourseMap.objects.filter(category_id__in=subCategoryIds, course_id=courseId).values_list("question_id")])

        print("subCategoryQuestionIds", subCategoryQuestionIds)
        print("\n")

        # get all the questions answered correctly, incorrectly or studied by the user
        userQuestionIds = set([x[0] for x in QuestionsUserMap.objects.filter(user_id=userId, course_id=courseId).values_list("question_id")])

        print("userQuestionIds", userQuestionIds)
        print("\n")

        # intersection of question ids solved by student and questions in the current category
        combinedQuestionIds = userQuestionIds.intersection(subCategoryQuestionIds)

        print("combinedQuestionIds", combinedQuestionIds)
        print("\n")

        # unlock the category of 3 or more questions from that category solved
        if len(combinedQuestionIds) >= 3:
            try:
                x = CategoryUserMap.objects.get(user_id=userId, category_id=categoryId)
            except:
                x = CategoryUserMap.objects.create(user_id=userId, category_id=categoryId)
            x.status = UNLOCKED
            x.save()
        
        # get all the kc_ids that the question involved
        kcsQuestionIds = [x[0] for x in  KCsQuestionsMap.objects.filter(question_id=questionId).values_list("kc_id")]

        print("kcsQuestionIds", kcsQuestionIds)
        print("\n")

        # create kc -> priority
        kcPriorityMap = {}
        for x in KCsUserMap.objects.filter(user_id=userId).values_list("kc_id", "priority"):
            kcPriorityMap[x[0]] = x[1]
        print("kcPriorityMap", kcPriorityMap)
        print("\n\n")

        # find the KCs which weren't studied, correct or incorrect but due to this interaction have been interacted with
        newKcs = list(set([x[0] for x in KCsUserMap.objects.filter(user_id=userId, priority=5, kc_id__in=kcsQuestionIds).values_list("kc_id")]))

        print("newKcs", newKcs)
        print("\n")


        # update the KCsStudentsMap
        # 1. get all the kc ids associated with the interacted question => kcsQuestionIds
        # 2. for each kc, check if the priority changes using the following matrix conversionMatrix[currPriority][currResponse]
        conversionMatrix = [
            [0, 0, 0],
            [1, 0, 1],
            [2, 1, 2],
            [3, 2, 3],
            [4, 2, 3],
            [4, 2, 3]
        ]
        kCsUserMap  = KCsUserMap.objects.filter(kc_id__in=kcsQuestionIds)
        kcsToUpdate = []
        for kc in kCsUserMap:
            currPriority = kcPriorityMap[kc.kc_id]
            newPriority  = conversionMatrix[currPriority][_status]
            if newPriority != currPriority:
                kc.priority = newPriority
                kcsToUpdate.append(kc)
        KCsUserMap.objects.bulk_update(kcsToUpdate, ["priority"])

        # mark as "familiar" any subcategory with <10% KCs with priority 5
        # 1. find the KCs which weren't in studied, correct or incorrect but due to this interaction have been interacted with
        # 2. find all Questions which involve these KCs
        # 3. find all subcategories related to these Questions
        # 4. mark the subcategories "familiar" if needed
        # get all the KCs of the combinedQuestions


        # 1. find the KCs which weren't studied, correct or incorrect but due to this interaction have been interacted with
        # newKcs has these values 
        
        # 2. find all Questions which involve these new KCs
        questionIds = list(set([x[0] for x in KCsQuestionsMap.objects.filter(kc__in=newKcs).values_list('question_id')]))
        
        print("questionIds", questionIds)
        print("\n")

        # 3. find all subcategories related to these Questions
        subCategoriesIds = list(set([x[0] for x in QuestionsCategoryCourseMap.objects.filter(question_id__in=questionIds, course_id=courseId).values_list('category_id')]))
        print("subCategoriesIds", subCategoriesIds)
        print("\n")

        # 4. mark the sub-categories "familiar" if needed
        categoryQuestionMap        = defaultdict(list)
        questionIds                = set()
        # create category -> question map
        questionsCategoryCourseMap = QuestionsCategoryCourseMap.objects.filter(category_id__in=subCategoriesIds, course_id=courseId).values_list("category_id", "question_id")
        for x in questionsCategoryCourseMap:
            categoryQuestionMap[x[0]].append(x[1])
            questionIds.add(x[1])
        questionIds = list(questionIds)
        print("categoryQuestionMap", categoryQuestionMap)
        print("\n\n")


        # create question -> kc map
        questionsKcMap  = defaultdict(set)
        kCsQuestionsMap = KCsQuestionsMap.objects.filter(question_id__in=questionIds)
        for x in kCsQuestionsMap:
            questionsKcMap[x.question_id].add(x.kc_id)
        print("questionsKcMap", questionsKcMap)
        print("\n\n")

        # create category -> kc
        categoryKcMap = defaultdict(set)
        for categoryId, questionIds in categoryQuestionMap.items():
            for questionId in questionIds:
                kcs = questionsKcMap[questionId]
                for kc in kcs:
                    categoryKcMap[categoryId].add(kc)
        print("categoryKcMap", categoryKcMap)
        print("\n\n")
        
        # do final calculations ie. if the category has < 10% 5 priority in these
        familiarCategories = []
        for categoryId, kcIds in categoryKcMap.items():
            total     = len(kcIds)
            priority5 = 0
            for kcId in kcIds:
                if kcPriorityMap[kcId] == 5:
                    priority5 += 1
            if priority5 / total * 1.0 <= 0.05:
                familiarCategories.append(categoryId)

        print("familiarCategories", familiarCategories)
        print("\n\n")
        
        
        # update the status of familiar sub-categories
        categories = CategoryUserMap.objects.filter(category_id__in=familiarCategories)
        for category in categories:
            category.status = FAMILIAR
        CategoryUserMap.objects.bulk_update(categories, ["status"])

        return Response({
            "msg": "all tables updated"
        }, status=status.HTTP_201_CREATED)
