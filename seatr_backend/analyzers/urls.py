from analyzers import views
from django.conf.urls import url

urlpatterns = [
    url(r'^analyzer/1$', views.Analyzer1.as_view()),
]