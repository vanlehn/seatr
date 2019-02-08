from django.db import models


from students import models as studentsModel
# Create your models here.


class Questions(models.Model):
    external_id = models.IntegerField()


STATUS_CHOICES = (
    (1, "solved"),
    (2, "studied/seen"),
)


class QuestionsStudentsMap(models.Model):
    students  = models.ForeignKey(studentsModel.Students, on_delete=models.PROTECT)
    questions = models.ForeignKey(Questions, on_delete=models.PROTECT)
    status    = models.IntegerField(choices=STATUS_CHOICES, default=2)

