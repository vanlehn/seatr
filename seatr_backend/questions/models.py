from django.db import models
from django.core.validators import MaxValueValidator, MinValueValidator

from users     import models as usersModel
from courses   import models as coursesModel
from questions import models as questionsModel


# explicit primary_key added to keep seatr in sync with ope
class Category(models.Model):
    external_id = models.IntegerField(primary_key=True)
    parent_id   = models.IntegerField(default=-1, null=False)
    order       = models.IntegerField(default=1,  null=False)


# explicit primary_key added to keep seatr in sync with ope
class Questions(models.Model):
    external_id = models.IntegerField(primary_key=True)
    

class KCs(models.Model):
    external_id = models.IntegerField(primary_key=True)
    importance  = models.IntegerField(null=True)


#### TODO: move this to mongo
class QuestionAttempts(models.Model):
    question = models.ForeignKey(Questions, on_delete=models.PROTECT)


class QuestionsCategoryCourseMap(models.Model):
    question = models.ForeignKey(Questions, on_delete=models.PROTECT)
    category = models.ForeignKey(Category, on_delete=models.PROTECT)
    course   = models.ForeignKey(coursesModel.Courses, on_delete=models.PROTECT)


STATUS_CHOICES = (
    (0, "studied"),
    (1, "correct"),
    (2, "incorrect")
)
class QuestionsUserMap(models.Model):
    user     = models.ForeignKey(usersModel.User, on_delete=models.PROTECT)
    question = models.ForeignKey(Questions, on_delete=models.PROTECT)
    status   = models.IntegerField(choices=STATUS_CHOICES, default=0, null=False)
    course   = models.ForeignKey(coursesModel.Courses, on_delete=models.PROTECT)


#### TODO: categories should have "locked", "unlocked"; subcategories should have "familiar", "unfamiliar"
STATUS_CHOICES = (
    (0, "locked"),
    (1, "unlocked"),
    (2, "familiar"),
    (3, "unfamiliar"),
    (4, "edge")

)
class CategoryUserMap(models.Model):
    user        = models.ForeignKey(usersModel.User, on_delete=models.PROTECT)
    category    = models.ForeignKey(Category, on_delete=models.PROTECT)
    status      = models.IntegerField(choices=STATUS_CHOICES, default=0, null=False)


# class CoursesQuestionsMap(models.Model):
#     course   = models.ForeignKey(coursesModel.Courses, on_delete=models.PROTECT)
#     question = models.ForeignKey(questionsModel.Questions, on_delete=models.PROTECT)


class KCsUserMap(models.Model):
    user     = models.ForeignKey(usersModel.User, on_delete=models.PROTECT)
    kc       = models.ForeignKey(KCs, on_delete=models.PROTECT)
    priority = models.IntegerField(validators=[MaxValueValidator(0), MinValueValidator(5)])


class KCsQuestionsMap(models.Model):
    kc       = models.ForeignKey(KCs, on_delete=models.PROTECT)
    question = models.ForeignKey(questionsModel.Questions, on_delete=models.PROTECT)


class CoursesKcsMap(models.Model):
    course = models.ForeignKey(coursesModel.Courses, on_delete=models.PROTECT)
    kc     = models.ForeignKey(KCs, on_delete=models.PROTECT)


class CategoryCourseMap(models.Model):
    category = models.ForeignKey(Category, on_delete=models.PROTECT)
    course   = models.ForeignKey(coursesModel.Courses, on_delete=models.PROTECT)
