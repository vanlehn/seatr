# http://fernandorodrigues.pro/creating-a-rest-apiwebservice-with-django-rest-framework-and-mongo-mongoengine-using-python-3/#Create_your_Models

from django.db   import models
from mongoengine import Document, EmbeddedDocument, fields
from users.models import User
from courses.models  import Courses


class Analyzers(models.Model):
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    course  = models.ForeignKey(Courses, on_delete=models.PROTECT)


class Analyzer1Properties(Document):
    id = fields.IntField(required=True)
    prop1 = fields.StringField(required=True)

