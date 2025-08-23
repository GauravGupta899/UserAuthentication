import json
import jwt
import datetime
from django.shortcuts import render
from django.http import JsonResponse
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from .models import User 
from django.contrib.auth.hashers import make_password, check_password


# JWT utility functions
def generate_jwt_token(user_id):
    payload = {
        'user_id': user_id,
        'exp': datetime.datetime.utcnow() + settings.JWT_EXPIRATION_DELTA
    }
    return jwt.encode(payload, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)

def verify_jwt_token(token):
    try:
        payload = jwt.decode(token, settings.JWT_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        return None
    except jwt.InvalidTokenError:
        return None

def jwt_required(view_func):
    def wrapper(request, *args, **kwargs):
        token = request.headers.get('Authorization')
        if not token:
            return JsonResponse({'error': 'Token missing'}, status=401)
        
        if token.startswith('Bearer '):
            token = token[7:]
        
        payload = verify_jwt_token(token)
        if not payload:
            return JsonResponse({'error': 'Invalid or expired token'}, status=401)
        
        request.user_id = payload['user_id']
        return view_func(request, *args, **kwargs)
    return wrapper


# Create your views here.

@csrf_exempt
def registration(request):
    if request.method == 'POST':
        data = json.loads(request.body.decode('utf-8'))
        username = data.get('username')
        email = data.get('email')
        password = data.get('password')

        if not username or not email or not password:
            return JsonResponse({
                'message': 'All fields are required'
            }, status=400)
        
        # Check if user already exists
        if User.objects.filter(username=username).exists():
            return JsonResponse({
                'message': 'Username already exists'
            }, status=400)
        
        if User.objects.filter(email=email).exists():
            return JsonResponse({
                'message': 'Email already exists'
            }, status=400)
        
        user = User(username=username, email=email, password=make_password(password))
        user.save()

        return JsonResponse({
            'message': 'User registered successfully'
        }, status=201)
    else:
        return JsonResponse({
            'message': 'Method not allowed'
        }, status=405)

@csrf_exempt
def login(request):
    if request.method == 'POST':
        data = json.loads(request.body.decode('utf-8'))
        username = data.get('username')
        password = data.get('password')

        if not username or not password:
            return JsonResponse({
                'message': 'Username and password are required'
            }, status=400)

        try:
            user = User.objects.get(username=username)
            if check_password(password, user.password):
                token = generate_jwt_token(user.id)
                return JsonResponse({
                    'message': 'Login successful',
                    'token': token,
                    'user': {
                        'id': user.id,
                        'username': user.username,
                        'email': user.email
                    }
                }, status=200)
            else:
                return JsonResponse({
                    'message': 'Invalid credentials'
                }, status=401)
        except User.DoesNotExist:
            return JsonResponse({
                'message': 'Invalid credentials'
            }, status=401)
    else:
        return JsonResponse({
            'message': 'Method not allowed'
        }, status=405)

@jwt_required
def dashboard(request):
    try:
        user = User.objects.get(id=request.user_id)
        return JsonResponse({
            'message': 'Welcome to dashboard',
            'user': {
                'id': user.id,
                'username': user.username,
                'email': user.email
            }
        })
    except User.DoesNotExist:
        return JsonResponse({
            'message': 'User not found'
        }, status=404)