# Generated by Django 2.1.5 on 2019-05-07 14:22

import django.core.validators
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Category',
            fields=[
                ('external_id', models.IntegerField(primary_key=True, serialize=False)),
                ('parent_id', models.IntegerField(default=-1)),
                ('order', models.IntegerField(default=1)),
            ],
        ),
        migrations.CreateModel(
            name='CategoryUserMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('status', models.IntegerField(choices=[(0, 'locked'), (1, 'unlocked'), (2, 'familiar'), (3, 'unfamiliar')], default=0)),
            ],
        ),
        migrations.CreateModel(
            name='CoursesKcsMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
        ),
        migrations.CreateModel(
            name='CoursesQuestionsMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
        ),
        migrations.CreateModel(
            name='KCs',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
        ),
        migrations.CreateModel(
            name='KCsQuestionsMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
        ),
        migrations.CreateModel(
            name='KCsUserMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('priority', models.IntegerField(validators=[django.core.validators.MaxValueValidator(0), django.core.validators.MinValueValidator(5)])),
            ],
        ),
        migrations.CreateModel(
            name='QuestionAttempts',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
        ),
        migrations.CreateModel(
            name='Questions',
            fields=[
                ('external_id', models.IntegerField(primary_key=True, serialize=False)),
            ],
        ),
        migrations.CreateModel(
            name='QuestionsUserMap',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('status', models.IntegerField(choices=[(0, 'studied'), (1, 'correct'), (2, 'incorrect')], default=0)),
                ('question', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to='questions.Questions')),
            ],
        ),
    ]
