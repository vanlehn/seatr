from rest_framework import serializers

from questions.models import Questions, KCs


class QuestionsSerializer(serializers.ModelSerializer):
    class Meta:
        model  = Questions
        fields = '__all__'


class KCsSerializer(serializers.ModelSerializer):
    class Meta:
        model  = KCs
        fields = '__all__'