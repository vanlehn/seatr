from questions import views
from django.conf.urls import url

urlpatterns = [
    url(r'^create-question/$', views.ListCreateQuestions.as_view()),
    url(r'^list-questions/$', views.ListCreateQuestions.as_view()),

    url(r'^update-create-status/$', views.UpdateCreateStatus.as_view()),

    url(r'^create-kc/$', views.ListCreateKCs.as_view()),
    url(r'^list-kcs/$', views.ListCreateKCs.as_view()),

    url(r'^mark-question-interaction/$', views.MarkQuestionInteraction.as_view()),
]