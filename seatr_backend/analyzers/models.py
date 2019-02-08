# http://fernandorodrigues.pro/creating-a-rest-apiwebservice-with-django-rest-framework-and-mongo-mongoengine-using-python-3/#Create_your_Models

from django.db   import models
from mongoengine import Document, EmbeddedDocument, fields
from students    import models as studentsModel
from courses     import models as coursesModel


class Analyzers(models.Model):
    student = models.ForeignKey(studentsModel.Students, on_delete=models.PROTECT)
    course  = models.ForeignKey(coursesModel.Courses, on_delete=models.PROTECT)


class Analyzer1Properties(Document):
    id = fields.IntField(required=True)
    prop1 = fields.StringField(required=True)

