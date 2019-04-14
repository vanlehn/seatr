from rest_framework.views    import APIView
from rest_framework.response import Response
from rest_framework          import status


class GetPostTest(APIView):
    def post(self, request, format=None):
        return Response({
            "data": "post test passed"
        }, status=status.HTTP_200_OK)

    def get(self, request, format=None):
        return Response({
            "data": "get test passed"
        }, status=status.HTTP_200_OK)
