# Generated by Django 2.1.5 on 2019-02-08 23:07

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('students', '0001_initial'),
        ('questions', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='QuestionsStudentsMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('status', models.IntegerField(choices=[(1, 'solved'), (2, 'studied/seen')], default=2)),
                ('questions', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to='questions.Questions')),
                ('students', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to='students.Students')),
            ],
        ),
        migrations.RemoveField(
            model_name='questionsstudentmap',
            name='questions',
        ),
        migrations.RemoveField(
            model_name='questionsstudentmap',
            name='students',
        ),
        migrations.DeleteModel(
            name='QuestionsStudentMap',
        ),
    ]
