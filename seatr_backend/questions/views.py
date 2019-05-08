from rest_framework                  import generics, status, exceptions
from rest_framework.views            import APIView
from rest_framework.response         import Response

# import the models
from questions.models import Questions, Category, QuestionsUserMap, QuestionAttempts, KCsUserMap, KCsQuestionsMap, KCs
from users.models  import User

# import the serializers
from questions.serializers import QuestionsSerializer, KCsSerializer

from collections import defaultdict

class ListCreateQuestions(generics.ListCreateAPIView):
    queryset         = Questions.objects.all()
    serializer_class = QuestionsSerializer
    pagination_class = None


class ListCreateKCs(generics.ListCreateAPIView):
    queryset         = KCs.objects.all()
    serializer_class = KCsSerializer
    pagination_class = None


class UpdateCreateStatus(APIView):
    # populates/updates the QuestionsStudentsMap table
    def post(self, request, format=None):
        STUDIED   = 0
        CORRECT   = 1
        INCORRECT = 2
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
            questions = Questions.objects.get(external_id=questionExternalId)
        except Questions.DoesNotExist:
            raise exceptions.NotFound("question_external_id " + str(questionExternalId) + " is invalid")
        try:
            student = Students.objects.get(external_id=studentExternalId)
        except Students.DoesNotExist:
            raise exceptions.NotFound("student_external_id " + str(studentExternalId) + " is invalid")

        questionStudentMap = QuestionsStudentsMap.objects.get_or_create(student=student, question=question)
        created = True if questionStudentMap[1] is True else False
        if created is True or questionStudentMap[0].status is not CORRECT:
            questionStudentMap[0].status = _status
            questionStudentMap[0].save()

        return Response({
            "text": "entry created"
        }, status=status.HTTP_201_CREATED)


class MarkQuestionInteraction(APIView):
    STUDIED    = 0
    CORRECT    = 1
    INCORRECT  = 2
    
    LOCKED     = 0
    UNLOCKED   = 1
    FAMILIAR   = 2
    UNFAMILIAR = 3

    # this function is called whenever a student interacts ie. attempts to answer or studies a question
    # it does the following:
    # 1. unlocking categories, marking subcategories familiar: when student interacts with a question, the status might change ie. unstudied -> correct, incorrect -> correct etc depending upon the interaction, new categories might be unlocked and subcategories might change to familiar
    def post(self, request, format=None):
        username   = int(request.user.external_id)
        _status    = int(request.query_params.get('status', None))
        studentId  = int(request.query_params.get('external_student_id', None))
        courseId   = int(request.query_params.get('external_course_id',  None))
        questionId = int(request.query_params.get('external_question_id',  None))
        QuestionAttempts.objects.create(question=questionId)
        # canChanged answers the question-- Can this interaction potentially "unlock" a new category or make a subcategory "familiar"?
        # this can only happen if the question is a new question ie. UNSTUDIED
        canChange = False
        # get the QuestionsStudentsMap for this student, course and question
        questionsStudentsMap = QuestionsStudentsMap.objects.get(student=studentId, course=courseId, question=questionId)
        
        currentStatus = questionsStudentsMap.status
        if currentStatus is None:
            canChange = True
            

        if _status not in [STUDIED, UNSTUDIED, INCORRECT]:
            return Response({
                "msg": "something is wrong, the question status can only be STUDIED, UNSTUDIED, INCORRECT but its" + " " + _status 
            }, status=status.HTTP_417_EXPECTATION_FAILED)
        
        if currentStatus is not None:
            # if student has already "correctly" solved the question don't do anything
            if currentStatus == CORRECT:
                return Response({
                    "msg": "question has already been answered correctly" 
                }, status=status.HTTP_200_OK)
            
            # if question is incorrect it can move only to studied and correct
            elif currentStatus == INCORRECT:
                if _status == INCORRECT:
                    return Response({
                        "msg": "the student made a repeated incorrect attempt" 
                    }, status=status.HTTP_200_OK)
                

        questionsStudentsMap.status = _status
        questionsStudentsMap.save(update_fields=['status'])
        
        if canChange:
            # check if due to the new interaction any category gets unlocked
            # get the sub-category and category of the question
            category       = Questions.objects.get(external_id=questionId).category
            parentCategory = Category.objects.get(id=category).reference

            # see if the new interaction changes the status of the category and subcategory
            # get all questions solved by user in that parentCategory
            # find all the subcategories of the category from Mongo
            subcategories  = CategorySubCategoryMap.get(parentCategory.id)
            # subcategories = Category.objects.filter(external_id__in=subcategories)

            # get all the questions answered correctly, incorrectly or studied by the user
            userQuestions = set(QuestionsStudentsMap.objects.filter(student=studentId, course=courseId))

            # get all the questions in the subcategories
            subcategoryCuestions = set(Questions.objects.filter(category__in=subcategories))

            # intersection of questions solved by student and questions in the current category
            combinedQuestions = userQuestions.intersection(subcategoryCuestions)

            # unlock the category of 3 or more questions from that category solved
            if len(combinedQuestions) >= 3:
                x = CategoryStudentMap.objects.get(student=studentId, category=parentCategory)
                x.status = UNLOCKED
            
            # mark as "familiar" any subcategory with <10% KCs with priority 5
            # 1. find the KCs which weren't in studied, correct or incorrect but due to this interaction have been interacted with
            # 2. find all Questions which involve these KCs
            # 3. find all subcategories related to these Questions
            # 4. mark the subcategories "familiar" if needed
            # get all the KCs of the combinedQuestions

            # get all the kcs that the question involved
            kcsQuestions   = KCsQuestionsMap.objects.filter(question=questionId)
            kcsQuestionIds = [x["kc"] for x in kcsQuestions]

            # 1. find the KCs which weren't in studied, correct or incorrect but due to this interaction have been interacted with
            kcsStudentIds = set([x["kc"] for x in KCsStudentsMap.objects.filter(student=studentId)])
            newKcs        = []
            for kc in kcsQuestionIds:
                if kc not in kcsStudentIds:
                    newKcs.append(kc)
            
            # 2. find all Questions which involve these KCs
            questions = KCsQuestionsMap.objects.filter(kc__in=newKcs).values_list('question')
            
            # 3. find all subcategories related to these Questions
            subcategories = Questions.objects.filter(external_id__in=questions).values_list('category')

            # 4. mark the categories "familiar" if needed
            # for each subcategory in subcategories, find the kcs in the subcategory and check if user has < 10% 5 priority in these
            KCsStudentsMap.objects.filter(student__in=studentId)
            for subcategory in subcategories:
                questions = subcategory.objects.all().values_list("questions")
                kc        = KCsQuestionsMap.objects.filter(question__in=questions).values_list("kc")
                kcStudent = KCsStudentsMap.objects.filter(kc__in=kc).values_list("priority")
                priority5 = 0
                for x in kcStudent:
                    if x == 5:
                        priority5 += 1
                if priority5 / len(kcStudent) <= 0.05:
                    x = CategoryStudentMap.objects.get(student=studentId, category=subcategory)
                    x.status = FAMILIAR
                    x.update_fields("status")

            # update the KCsStudentsMap
            # 1. get all the kcs associated with the interacted question
            # 2. get all the questions associated with that kc
            # 3. calculate how many correct, incorrect, studied questions
            # 4. based on 3. assign kc its priority

            kcScoreMap = defaultdict(defaultdict)
            # 1. get all the kcs associated with the interacted question
            # 2. get all the questions associated with that kc
            # kcsQuestions stores that
            # {kc: [q1, q2, q3]}
            kcsQuestionsMap = defaultdict(list)
            finalKcScore    = defaultdict(int) 
            # {q1: [kc1, kc2, kc3]}
            questionsKcsMap = defaultdict(list) 
            for kcsQuestion in kcsQuestions:
                kcsQuestionsMap[kcsQuestion["kc"]].append(kcsQuestion["question"])
                questionsKcsMap[kcsQuestion["question"]].append(kcsQuestion["kc"])
                questions.append(kcsQuestion["question"])
            # 3. calculate how many correct, incorrect, studied questions
            questions = QuestionsStudentsMap.objects.filter(question__in=questions)
            for question in questions:
                    for kc in questionsKcsMap[question["id"]]:
                        kcScoreMap[kc][question["status"]] += 1
            # 4. based on 3. assign kc its priority
            for kc, score in kcScoreMap.items():
                # kc never seen by student
                # if kc not in kcScoreMap:
                #   finalKcScore[kc] = 5
                # at least 1 studied but not in solved
                if kcScoreMap[kc][self.STUDIED] >= 1 and kcScoreMap[kc][self.CORRECT] == 0:
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
            # 5. write to db
            ########## FIX THIS
            for kc, score in finalKcScore.items():
                KCsStudentsMap.objects.create()

            return Response({
                "msg": "all tables updated"
            }, status=status.HTTP_201_CREATED)
