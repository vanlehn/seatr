from rest_framework import serializers

from kcs.models import KCs


class KCsSerializer(serializers.ModelSerializer):
    class Meta:
        model  = KCs
        fields = '__all__'