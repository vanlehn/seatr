from django.shortcuts import render

# Create your views here.

from rest_framework  import generics

# import the models
from questions.models import Questions

# import the serializers
from questions.serializers import QuestionsSerializer


class ListCreateQuestions(generics.ListCreateAPIView):
    queryset         = Questions.objects.all()
    serializer_class = QuestionsSerializer
    pagination_class = None