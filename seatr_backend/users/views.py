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


#### TODO: convert to generics.CreateAPIView
class GetCreateTokenView(APIView):
    pagination_class   = None
    permission_classes = (AllowAny, )

    def post(self, request):
        try:
            external_id = int(request.data.get('external_id'))
        except TypeError as e:
            raise TypeError("external_id can't be None") from e
        except ValueError as e:
            raise ValueError("the data type of external_id is incorrect") from e

        try:
            user = User.objects.get(external_id=external_id)
        except User.DoesNotExist:
            raise exceptions.NotFound("external_id " + str(external_id) + " doesn't exist")

        token, created  = Token.objects.get_or_create(user=user)
        if created is False:
            return Response({
                "token": str(token),
                "msg"  : "token already exists" 
                }, status=status.HTTP_200_OK)
        Response({
            "token": str(token),
            "msg"  : "token created"
            }, status=status.HTTP_201_CREATED)


# class GetCreateTokenView(generics.ListCreateAPIView):
#     pagination_class   = None
#     permission_classes = (AllowAny, )
#     queryset           = Token.objects.all()
#     serializer_class   = TokenSerializer