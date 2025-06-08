from django.contrib.auth.models import Group, User
from rest_framework import serializers
from api.models import Contest, Photo, Vote
from datetime import datetime

class UserSerializer(serializers.HyperlinkedModelSerializer):
    photos = serializers.SerializerMethodField()
    groups = serializers.PrimaryKeyRelatedField(queryset=Group.objects.all(), many=True)


    # This method retrieves the photos associated with the user.
    def get_photos(self, obj):
        request = self.context.get('request')
        photos = Photo.objects.filter(owner=obj)
        return [request.build_absolute_uri(photo.image.url) for photo in photos]

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'groups', 'photos', 'last_login']


class RoleSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Group
        fields = ['id', 'name']


class ContestSerializer(serializers.HyperlinkedModelSerializer):
    start_date = serializers.DateTimeField()
    end_date = serializers.DateTimeField()
    voting_start_date = serializers.DateTimeField()
    voting_end_date = serializers.DateTimeField()

    def to_internal_value(self, data):
        # Se intenta parsear fechas en varios formatos antes de la validación estándar
        for field in ['start_date', 'end_date', 'voting_start_date', 'voting_end_date']:
            date_str = data.get(field)
            if date_str and not self.is_iso_format(date_str):
                # Intenta convertir desde formato tipo "Jun 8, 2025"
                try:
                    parsed_date = datetime.strptime(date_str, "%b %d, %Y")
                    data[field] = parsed_date.isoformat()  # Pasa a ISO 8601
                except ValueError:
                    pass  # deja que el validador original capture error

        return super().to_internal_value(data)

    def is_iso_format(self, s):
        # Simple check para ver si la cadena está en ISO 8601
        try:
            datetime.fromisoformat(s.replace("Z", "+00:00"))
            return True
        except ValueError:
            return False

    class Meta:
        model = Contest
        fields = ['id', 'name', 'description', 'start_date', 'end_date',
                  'voting_start_date', 'voting_end_date', 'max_photos_per_user']


class PhotoSerializer(serializers.HyperlinkedModelSerializer):
    owner_name = serializers.CharField(source='owner.username', read_only=True)
    uri = serializers.SerializerMethodField()
    # This method generates a URI for the photo.
    def get_uri(self, obj):
        request = self.context.get('request')
        return request.build_absolute_uri(f'/photos/{obj.id}/')

    class Meta:
        model = Photo
        fields = ['id', 'image', 'name', 'description', 'owner_name', 'owner','status', 'upload_date', 'votes', 'contest', 'uri']
        read_only_fields = ['owner_name', 'votes', 'uri']


class VoteSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Vote
        fields = ['id', 'user', 'photo', 'timestamp']
