from django.shortcuts import render

# Create your views here.

from rest_framework  import generics

# import the models
from users.models import User

# import the serializers
from users.serializers import UsersSerializer


class ListCreateStudents(generics.ListCreateAPIView):
    queryset         = User.objects.all()
    serializer_class = UsersSerializer
    pagination_class = None

