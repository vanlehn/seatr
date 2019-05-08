from rest_framework                  import serializers
from rest_framework.authtoken.models import Token

from users.models import User


class UsersSerializer(serializers.ModelSerializer):
    class Meta:
        model  = User
        fields = '__all__'


# class TokenSerializer(serializers.ModelSerializer):
#     # user = UsersSerializer(read_only=True)
#     def getUser(self, obj):
#         uid = self.context['request'].POST["external_id"]
#         return UsersSerializer.data()
    
#     user = serializers.SerializerMethodField('getUser')
#     class Meta:
#         model  = Token
#         fields = ('user', )