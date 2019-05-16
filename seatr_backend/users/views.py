from rest_framework                  import generics, status, exceptions
from rest_framework.views            import APIView
from rest_framework.authtoken.models import Token
from rest_framework.response         import Response

from rest_framework.permissions import AllowAny

from .models      import User, Platform
from .serializers import UsersSerializer, PlatformSerializer

from rest_framework import viewsets



class ListCreateUsers(generics.ListCreateAPIView):
    queryset         = User.objects.all()
    serializer_class = UsersSerializer
    pagination_class = None


class CreatePlatform(viewsets.ModelViewSet):
    queryset           = Platform.objects.all()
    serializer_class   = PlatformSerializer
    pagination_class   = None
    permission_classes = (AllowAny, )