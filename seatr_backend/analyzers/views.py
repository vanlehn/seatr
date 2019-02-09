from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from questions.models import Questions, QuestionsStudentsMap
from kcs.models       import KCsQuestionsMap


class Analyzer1(APIView):
    def post(self, request, format=None):
        # get the post data: studentId and questionIds of the candidate questions given by OPE
        questionIds = request.POST.get('questionIds', None)
        studentId   = int(request.POST.get('studentId', None))

        # get all the questions solved by the student and get the KCs of those questions
        # ie. get all the KCs that the student has seen or mastered
        questionSolvedIds    = QuestionsStudentsMap.objects.filter(student__in=[studentId])
        kcIdsSolvedQuestions = KCsQuestionsMap.objects.filter(question__in=[2, 3])

        return Response({
            "kcs": kcIdsSolvedQuestions
        }, status=status.HTTP_200_OK)
