from rest_framework  import generics

# import the models
from courses.models import Courses

# import the serializers
from courses.serializers import CoursesSerializer


class ListCreateCourses(generics.ListCreateAPIView):
    queryset         = Courses.objects.all()
    serializer_class = CoursesSerializer
    pagination_class = None