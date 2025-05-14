from django.contrib.auth.models import Group, User
from rest_framework import permissions, viewsets
from rest_framework.exceptions import PermissionDenied

from .serializers import RoleSerializer, UserSerializer, ContestSerializer, PhotoSerializer, VoteSerializer
from api.models import Contest, Photo, Vote
from rest_framework.permissions import IsAuthenticated, AllowAny, IsAdminUser
from rest_framework.authtoken.models import Token

class UserViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = User.objects.all().order_by('-date_joined')
    serializer_class = UserSerializer
    def get_permissions(self):
        if self.action in ['create', 'list'] :
            return [AllowAny()]
        elif self.action in ['update', 'partial_update', 'retrieve']:
            return [IsAuthenticated()]
        else:
            return [IsAdminUser()]

    # Create a new user and set the password if provided.
    def perform_create(self, serializer):
        user = serializer.save()
        password = self.request.data.get('password')
        if password:
            user.set_password(password)
            user.save()
        Token.objects.create(user=user)
class RoleViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows roles to be viewed or edited.
    """
    queryset = Group.objects.all().order_by('name')
    serializer_class = RoleSerializer
    permission_classes = [permissions.IsAdminUser]


class ContestViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows contests to be viewed or edited.
    """
    def get_permissions(self):
        if self.action in ['list'] :
            return [AllowAny()]
        elif self.action in ['create', 'update', 'partial_update', 'retrieve']:
            return [IsAuthenticated()]
        else:
            return [IsAdminUser()]
    
    queryset = Contest.objects.all().order_by('name')
    serializer_class = ContestSerializer
    # permission_classes = [permissions.IsAdminUser]


class PhotoViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows photos to be viewed or edited.
    """
    from django_filters.rest_framework import DjangoFilterBackend

    queryset = Photo.objects.all().order_by('upload_date')
    serializer_class = PhotoSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['contest']


    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)

    def perform_update(self, serializer, *args, **kwargs):
        UPDATE_ERROR_MSG = "You cannot update this photo because you are not the owner."
        if self.request.user != serializer.owner:
            raise PermissionDenied(UPDATE_ERROR_MSG)
        return super().perform_update(serializer)

    def perform_destroy(self, serializer, *args, **kwargs):
        DELETE_ERROR_MSG = "You cannot delete this photo because you are not the owner."
        if self.request.user != serializer.owner:
            raise PermissionDenied(DELETE_ERROR_MSG)
        return super().perform_destroy(serializer)


class VoteViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows votes to be viewed or edited.
    """
    queryset = Vote.objects.all().order_by('timestamp')
    serializer_class = VoteSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    def perform_update(self, serializer, *args, **kwargs):
        UPDATE_ERROR_MSG = "You cannot update this vote because you are not the owner."
        if self.request.user != serializer:
            raise PermissionDenied(UPDATE_ERROR_MSG)
        return super().perform_update(serializer)

    def perform_destroy(self, serializer, *args, **kwargs):
        DELETE_ERROR_MSG = "You cannot delete this vote because you are not the owner."
        if self.request.user != serializer:
            raise PermissionDenied(DELETE_ERROR_MSG)
        return super().perform_destroy(serializer)
