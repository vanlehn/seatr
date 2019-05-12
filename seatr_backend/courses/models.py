from django.db import models

from users  import models as usersModel


class Courses(models.Model):
    external_id = models.IntegerField(primary_key=True, unique=True, null=False)
    description = models.CharField(null=True, max_length=2000)


class CoursesUserMap(models.Model):
    course  = models.ForeignKey(Courses, on_delete=models.PROTECT)
    user    = models.ForeignKey(usersModel.User, on_delete=models.PROTECT)