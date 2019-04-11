from django.shortcuts import render

from rest_framework          import generics, status, exceptions
from rest_framework.views    import APIView
from rest_framework.response import Response

# import the models
from questions.models import Questions, QuestionsStudentsMap
from students.models  import Students

# import the serializers
from questions.serializers import QuestionsSerializer


class ListCreateQuestions(generics.ListCreateAPIView):
    queryset         = Questions.objects.all()
    serializer_class = QuestionsSerializer
    pagination_class = None


class UpdateCreateStatus(APIView):
    def post(self, request, format=None):

        STUDIED = 1
        CORRECT = 2
        INCORRECT = 3
        try:
            questionExternalId = int(request.data.get('external_task_id'))
            studentExternalId  = int(request.data.get('external_student_id'))
            _status            = request.data.get('status')
            if _status == "studied":
                _status = 1
            elif _status == "correct":
                _status = 2
            else:
                _status = 3
        except TypeError as e:
            raise TypeError("POST parameters can't be None") from e
        except ValueError as e:
            raise ValueError("the data type of POST parameters are incorrect") from e

        # get the student.id, question.id, from the externalIds
        try:
            questions = Questions.objects.filter(external_id=questionExternalId)
        except Questions.DoesNotExist:
            raise exceptions.NotFound("question_external_id" + questionExternalId + "is invalid")
        try:
            student = Students.objects.get(external_id=studentExternalId)
        except Students.DoesNotExist:
            raise exceptions.NotFound("student_external_id" + studentExternalId + "is invalid")

        for question in questions:
            questionStudentMap = QuestionsStudentsMap.objects.get_or_create(student=student, question=question)
            if questionStudentMap[1] is True or questionStudentMap[0].status is None or questionStudentMap[0].status == INCORRECT:
                questionStudentMap[0].status = _status
                questionStudentMap[0].save()

        return Response(status=status.HTTP_201_CREATED)
