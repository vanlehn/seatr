from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

class Analyzer1(APIView):
    def post(self, request, format=None):
        questions = request.POST.get('questions', None)
        questions = [x for x in range(10)]
        return Response({
            "questions": questions
        }, status=status.HTTP_200_OK)
