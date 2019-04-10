from django.db import models


from students import models as studentsModel
# Create your models here.


class Questions(models.Model):
    external_id = models.IntegerField()


STATUS_CHOICES = (
    (1, "studied"),
    (2, "correct"),
    (3, "incorrect")
)


class QuestionsStudentsMap(models.Model):
    student  = models.ForeignKey(studentsModel.Students, on_delete=models.PROTECT)
    question = models.ForeignKey(Questions, on_delete=models.PROTECT)
    status   = models.IntegerField(choices=STATUS_CHOICES, default=2)

