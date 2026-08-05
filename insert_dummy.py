import urllib.request
import urllib.error
import json
import random
import string
import time

base_url = 'http://localhost:8080'

def req(method, path, body=None, token=None):
    url = base_url + path
    data = json.dumps(body).encode('utf-8') if body is not None else None
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    r = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(r) as res:
            return json.loads(res.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        err_body = e.read().decode('utf-8')
        return {'error': e.code, 'body': err_body}
    except Exception as e:
        print(f'Exception: {e}')
        return None

def random_lower(length=6):
    return ''.join(random.choices(string.ascii_lowercase, k=length))

# 1. 개발자 계정 생성 및 로그인
dev_token = None
for _ in range(5):
    dev_id = f"devadmin{random_lower(4)}"
    dev_pw = "Admin1234!"
    
    phone = f"010-{random.randint(1000,9999)}-{random.randint(1000,9999)}"
    print(f"Attempting to register dev_id: {dev_id}, phone: {phone}")
    reg_res = req('POST', '/auth/dev/register', {
        'userId': dev_id,
        'password': dev_pw,
        'name': '개발자',
        'phoneNumber': phone,
        'secret': 'dummy-dev-secret'
    })
    print(f"Reg Result: {reg_res}")
    
    if reg_res and 'data' in reg_res:
        print(f"Dev account created: {dev_id}")
        login_res = req('POST', '/auth/user/login', {'userId': dev_id, 'password': dev_pw})
        if login_res and 'data' in login_res and login_res['data']:
            dev_token = login_res['data'].get('accessToken')
            print(f"Dev login success! Token obtained.")
            break

if not dev_token:
    print("Failed to acquire dev token.")
    exit(1)

# 2. 동아리 데이터 목록 생성
clubs_data = [
    {'name': '봉사동아리', 'cat': '봉사', 't1': '봉사', 't2': '나눔', 't3': '사회', 'intro': '따뜻한 사회를 만드는 봉사동아리입니다.', 'pres': '김봉사'},
    {'name': 'IT연구회', 'cat': '학술', 't1': 'IT', 't2': '개발', 't3': '코딩', 'intro': '최신 기술을 함께 연구하는 학술동아리입니다.', 'pres': '이학술'},
    {'name': '댄스팀', 'cat': '공연', 't1': '댄스', 't2': '공연', 't3': '무대', 'intro': '무대 위에서 빛나는 댄스동아리입니다.', 'pres': '박공연'},
    {'name': '등산클럽', 'cat': '운동', 't1': '등산', 't2': '운동', 't3': '자연', 'intro': '자연과 함께하는 등산동아리입니다.', 'pres': '최운동'},
    {'name': '음악밴드', 'cat': '공연', 't1': '음악', 't2': '밴드', 't3': '기타', 'intro': '함께 연주하고 노래하는 밴드동아리입니다.', 'pres': '정음악'},
    {'name': '사진동아리', 'cat': '취미교양', 't1': '사진', 't2': '카메라', 't3': '출사', 'intro': '아름다운 순간을 기록하는 사진동아리입니다.', 'pres': '강사진'}
]

created_count = 0
for c in clubs_data:
    club_user_id = f"club{random_lower(6)}"
    club_pw = "Club12345!"
    
    # 계정 생성
    reg_club = req('POST', '/auth/user/register', {
        'userId': club_user_id,
        'password': club_pw,
        'name': c['pres'],
        'phoneNumber': '010-0000-0001'
    }, dev_token)
    
    if not reg_club or 'data' not in reg_club:
        print(f"Failed to register club user {club_user_id}: {reg_club}")
        continue
        
    # 로그인
    login_club = req('POST', '/auth/user/login', {
        'userId': club_user_id,
        'password': club_pw
    })
    
    ctoken = login_club.get('data', {}).get('accessToken') if login_club and 'data' in login_club and login_club['data'] else None
    if not ctoken:
        print(f"Failed to login for club user {club_user_id}")
        continue
        
    # 동아리 ID 조회
    my_club = req('POST', '/auth/user/find/club', {}, ctoken)
    club_id = my_club.get('data', {}).get('clubId') if my_club and 'data' in my_club and my_club['data'] else None
    if not club_id:
        print(f"Failed to get clubId for {club_user_id}")
        continue
        
    # 동아리 정보 업데이트
    info_payload = {
        'name': c['name'],
        'category': c['cat'],
        'division': '중동',
        'tags': [c['t1'], c['t2'], c['t3']],
        'introduction': c['intro'],
        'presidentName': c['pres'],
        'presidentPhoneNumber': '010-0000-0001',
        'description': {
            'introDescription': c['intro'],
            'activityDescription': f"{c['name']} 활동 소개",
            'awards': [],
            'idealCandidate': None,
            'benefits': None,
            'faqs': []
        },
        'socialLinks': {}
    }
    up_res = req('PUT', '/api/club/info', info_payload, ctoken)
    if up_res and 'data' in up_res:
        created_count += 1
        print(f"[{created_count}/6] 동아리 생성 완료: {c['name']} (ID: {club_id})")

# 결과 검색 확인
search_res = req('GET', '/api/club/search/')
total_clubs = search_res.get('data', {}).get('totalCount') if search_res and 'data' in search_res and search_res['data'] else 0
print(f"\n🎉 성공적으로 더미 동아리 데이터를 생성했습니다! (현재 총 동아리 수: {total_clubs}개)")
