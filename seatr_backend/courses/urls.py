from courses import views
from django.conf.urls import url

urlpatterns = [
    url(r'^create-course/$', views.ListCreateCourses.as_view()),
    url(r'^list-courses/$', views.ListCreateCourses.as_view()),
]