from django.shortcuts import render

# Create your views here.

from rest_framework  import generics

# import the models
from students.models import Students

# import the serializers
from students.serializers import StudentsSerializer


class ListCreateStudents(generics.ListCreateAPIView):
    queryset         = Students.objects.all()
    serializer_class = StudentsSerializer
    pagination_class = None

