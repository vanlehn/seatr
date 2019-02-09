from django.shortcuts import render

# Create your views here.

from rest_framework  import generics

# import the models
from kcs.models import KCs

# import the serializers
from kcs.serializers import KCsSerializer


class ListCreateKCs(generics.ListCreateAPIView):
    queryset         = KCs.objects.all()
    serializer_class = KCsSerializer
    pagination_class = None
