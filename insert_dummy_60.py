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
    reg_res = req('POST', '/auth/dev/register', {
        'userId': dev_id,
        'password': dev_pw,
        'name': '개발자',
        'phoneNumber': phone,
        'secret': 'dummy-dev-secret'
    })
    if reg_res and 'data' in reg_res:
        login_res = req('POST', '/auth/user/login', {'userId': dev_id, 'password': dev_pw})
        if login_res and 'data' in login_res and login_res['data']:
            dev_token = login_res['data'].get('accessToken')
            print(f"Dev login success with ID: {dev_id}")
            break

if not dev_token:
    print("Failed to acquire dev token.")
    exit(1)

# 60개 동아리 목록 정의
clubs_data = [
    # 학술 (10)
    {'name': 'IT연구회', 'cat': '학술', 'tags': ['학술', 'IT', '개발', '코딩'], 'intro': '최신 기술과 소프트웨어 개발을 연구하는 학술동아리입니다.', 'pres': '이학술'},
    {'name': 'AI데이터랩', 'cat': '학술', 'tags': ['학술', 'AI', '빅데이터', '머신러닝'], 'intro': '인공지능과 데이터 분석 실전 프로젝트를 진행하는 동아리입니다.', 'pres': '김인공'},
    {'name': '알고리즘마스터', 'cat': '학술', 'tags': ['학술', '코딩테스트', '알고리즘', '컴공'], 'intro': '코딩 테스트 대비 및 핵심 알고리즘을 스터디하는 동아리입니다.', 'pres': '박알고'},
    {'name': '로봇공학회', 'cat': '학술', 'tags': ['학술', '로봇', '임베디드', '하드웨어'], 'intro': '아두이노, 라즈베리파이로 나만의 로봇을 제작하는 학술동아리입니다.', 'pres': '최로봇'},
    {'name': '경제경영연구회', 'cat': '학술', 'tags': ['학술', '경제', '경영', '시사'], 'intro': '글로벌 경제 동향 분석과 경영 사례를 논의하는 동아리입니다.', 'pres': '정경영'},
    {'name': '금융투자학회', 'cat': '학술', 'tags': ['학술', '주식', '금융', '투자'], 'intro': '건전한 금융 지식 습득과 모의 주식 투자를 연구하는 동아리입니다.', 'pres': '한금융'},
    {'name': '스타트업랩', 'cat': '학술', 'tags': ['학술', '창업', '스타트업', '비즈니스'], 'intro': '혁신적인 아이디어를 사업화하고 창업 경진대회를 준비합니다.', 'pres': '윤창업'},
    {'name': '천문관측회', 'cat': '학술', 'tags': ['학술', '천문', '우주', '관측'], 'intro': '밤하늘 별자리 관측과 천문학 이론을 학습하는 동아리입니다.', 'pres': '강우주'},
    {'name': '역사탐구회', 'cat': '학술', 'tags': ['학술', '역사', '답사', '인문학'], 'intro': '한국사와 세계사 유적지를 답사하고 논의하는 인문학 동아리입니다.', 'pres': '조역사'},
    {'name': '생명과학연구회', 'cat': '학술', 'tags': ['학술', '생명과학', '바이오', '실험'], 'intro': '최신 바이오 기술과 생명과학 최신 트렌드를 탐구합니다.', 'pres': '임바이오'},

    # 봉사 (10)
    {'name': '봉사동아리', 'cat': '봉사', 'tags': ['봉사', '나눔', '사회', '따뜻함'], 'intro': '지역사회 이웃들에게 따뜻한 손길을 전하는 봉사동아리입니다.', 'pres': '김봉사'},
    {'name': '해바라기봉사단', 'cat': '봉사', 'tags': ['봉사', '아동', '돌봄', '교육'], 'intro': '지역 아동센터 아이들에게 학습 보조 및 정서 지원을 합니다.', 'pres': '나아동'},
    {'name': '유기동물보호회', 'cat': '봉사', 'tags': ['봉사', '유기견', '동물권', '보호소'], 'intro': '유기동물 보호소 봉사와 동물 권익 보호 캠페인을 진행합니다.', 'pres': '동보호'},
    {'name': '환경정화단', 'cat': '봉사', 'tags': ['봉사', '플로깅', '환경보호', '제로웨이스트'], 'intro': '캠퍼스와 해변 플로깅을 진행하는 환경 보호 동아리입니다.', 'pres': '송환경'},
    {'name': '교육봉사단멘토', 'cat': '봉사', 'tags': ['봉사', '멘토링', '청소년', '재능기부'], 'intro': '청소년들의 진로 상담과 과목 멘토링을 수행하는 동아리입니다.', 'pres': '류멘토'},
    {'name': '사랑의연탄나눔', 'cat': '봉사', 'tags': ['봉사', '계절봉사', '독거노인', '나눔'], 'intro': '겨울철 독거노인 연탄 배달 및 생필품 지원을 전달합니다.', 'pres': '차나눔'},
    {'name': '벽화봉사단디자인', 'cat': '봉사', 'tags': ['봉사', '벽화', '미술', '디자인'], 'intro': '어두운 골목길과 낙후된 담장에 알록달록 벽화를 그립니다.', 'pres': '신벽화'},
    {'name': '다문화가지원단', 'cat': '봉사', 'tags': ['봉사', '다문화', '한국어', '문화교류'], 'intro': '다문화 가정 이주민분들의 적응과 한국어 교육을 지원합니다.', 'pres': '백문화'},
    {'name': '헌혈사랑봉사단', 'cat': '봉사', 'tags': ['봉사', '헌혈', '생명나눔', '캠페인'], 'intro': '정기적인 헌혈 참여와 헌혈증 기부 캠페인을 적극 실시합니다.', 'pres': '홍헌혈'},
    {'name': '재능기부음악 봉사단', 'cat': '봉사', 'tags': ['봉사', '음악', '위로', '재능기부'], 'intro': '요양원과 병원을 방문하여 따뜻한 연주회를 선사합니다.', 'pres': '서위로'},

    # 공연 (10)
    {'name': '댄스팀', 'cat': '공연', 'tags': ['공연', '댄스', '무대', '방송댄스'], 'intro': '무대 위에서 화려하고 에너제틱하게 빛나는 댄스동아리입니다.', 'pres': '박공연'},
    {'name': '음악밴드', 'cat': '공연', 'tags': ['공연', '음악', '밴드', '기타'], 'intro': '자작곡과 다양한 커버 곡을 연주하는 정통 락/팝 밴드입니다.', 'pres': '정음악'},
    {'name': '스트릿댄스크루', 'cat': '공연', 'tags': ['공연', '힙합', '팝핀', '비보잉'], 'intro': '힙합, 팝핀, 락킹 등 스트릿 스트릿 댄스를 열정적으로 춥니다.', 'pres': '오스트릿'},
    {'name': '통기타울림', 'cat': '공연', 'tags': ['공연', '통기타', '어쿠스틱', '감성'], 'intro': '잔잔한 감성의 어쿠스틱 통기타와 보컬 동아리입니다.', 'pres': '황통기타'},
    {'name': '극단무대', 'cat': '공연', 'tags': ['공연', '연극', '연기', '대본'], 'intro': '대본 정독부터 무대 연출, 연기까지 직접 정기 공연을 만듭니다.', 'pres': '문연극'},
    {'name': '뮤지컬컴퍼니', 'cat': '공연', 'tags': ['공연', '뮤지컬', '노래', '무대'], 'intro': '화려한 춤과 노래, 명작 뮤지컬 무대를 함께 만듭니다.', 'pres': '양뮤지컬'},
    {'name': '아카펠라하모니', 'cat': '공연', 'tags': ['공연', '아카펠라', '화음', '보컬'], 'intro': '악기 없이 오직 목소리의 화음만으로 아름다운 곡을 노래합니다.', 'pres': '변화음'},
    {'name': '풍물패신명', 'cat': '공연', 'tags': ['공연', '풍물', '사물놀이', '전통'], 'intro': '우리 소리와 꽹과리, 장구의 신명 나는 가락을 연주합니다.', 'pres': '하전통'},
    {'name': '재즈앙상블', 'cat': '공연', 'tags': ['공연', '재즈', '피아노', '색소폰'], 'intro': '스윙, 보사노바 등 수준 높은 재즈 곡들을 합주합니다.', 'pres': '고재즈'},
    {'name': '힙합클럽비트', 'cat': '공연', 'tags': ['공연', '힙합', '랩', '비트메이킹'], 'intro': '직접 비트를 찍고 랩 가사를 써서 힙합 정기공연을 엽니다.', 'pres': '남힙합'},

    # 운동 (10)
    {'name': '등산클럽', 'cat': '운동', 'tags': ['운동', '등산', '자연', '트레킹'], 'intro': '전국의 아름다운 명산을 찾아 등산하고 트레킹하는 동아리입니다.', 'pres': '최운동'},
    {'name': '풋살매니아', 'cat': '운동', 'tags': ['운동', '풋살', '축구', '매주경기'], 'intro': '매주 정기 풋살 경기를 진행하며 체력을 단련하고 교류합니다.', 'pres': '노풋살'},
    {'name': '농구동아리슬램', 'cat': '운동', 'tags': ['운동', '농구', '3on3', '코트'], 'intro': '화려한 슛과 실전 매치로 우정을 쌓는 농구 동아리입니다.', 'pres': '하농구'},
    {'name': '테니스아카데미', 'cat': '운동', 'tags': ['운동', '테니스', '코트', '라켓'], 'intro': '초보자 레슨부터 포핸드, 백핸드 정기 코치 프로그램을 운영합니다.', 'pres': '곽테니스'},
    {'name': '배드민턴클럽', 'cat': '운동', 'tags': ['운동', '배드민턴', '셔틀콕', '민턴'], 'intro': '남녀노소 누구나 재밌고 신나게 즐기는 배드민턴 동아리입니다.', 'pres': '성민턴'},
    {'name': '클라이밍크루', 'cat': '운동', 'tags': ['운동', '볼더링', '암벽등반', '실내클라이밍'], 'intro': '실내 암벽장 볼더링 문제 해결과 야외 암벽 조망을 다닙니다.', 'pres': '배암벽'},
    {'name': '수영사랑', 'cat': '운동', 'tags': ['운동', '수영', '체력', '자유형'], 'intro': '새벽 및 저녁 정기 수영과 수중 레크리에이션을 진행합니다.', 'pres': '전수영'},
    {'name': '탁구교실', 'cat': '운동', 'tags': ['운동', '탁구', '핑퐁', '단식복식'], 'intro': '빠른 템포의 스매시와 손에 땀을 쥐게 하는 탁구 매치 동아리입니다.', 'pres': '유탁구'},
    {'name': '러닝크루', 'cat': '운동', 'tags': ['운동', '러닝', '마라톤', '조깅'], 'intro': '캠퍼스와 수변 공원을 달리며 나만의 페이스를 찾는 러닝 크루입니다.', 'pres': '심러닝'},
    {'name': '볼링클럽스트라이크', 'cat': '운동', 'tags': ['운동', '볼링', '스트라이크', '핀'], 'intro': '통쾌한 스트라이크의 쾌감을 함께 느끼는 볼링 동아리입니다.', 'pres': '주볼링'},

    # 취미교양 (10)
    {'name': '사진동아리', 'cat': '취미교양', 'tags': ['취미교양', '사진', '카메라', '출사'], 'intro': '아름다운 순간과 풍경을 카메라 렌즈로 기록하는 출사 동아리입니다.', 'pres': '강사진'},
    {'name': '보드게임연구회', 'cat': '취미교양', 'tags': ['취미교양', '보드게임', '전략', '친목'], 'intro': '카탄, 루미큐브부터 전략 보드게임까지 다양하게 플레이합니다.', 'pres': '원보드'},
    {'name': '베이킹클럽', 'cat': '취미교양', 'tags': ['취미교양', '제과제빵', '디저트', '쿠키'], 'intro': '달콤한 쿠키와 케이크, 갓 구운 빵을 직접 구워 나누는 베이킹 모임입니다.', 'pres': '천디저트'},
    {'name': '영화비평회', 'cat': '취미교양', 'tags': ['취미교양', '영화', '시네마', '토론'], 'intro': '독립영화와 단편영화, 감명 깊은 명작을 함께 관람하고 토론합니다.', 'pres': '방영화'},
    {'name': '캘리그라피와 손글씨', 'cat': '취미교양', 'tags': ['취미교양', '캘리그라피', '손글씨', '붓펜'], 'intro': '붓펜과 만년필로 예쁜 글귀를 적어 작품을 만드는 감성 동아리입니다.', 'pres': '공글씨'},
    {'name': '여행크루떠나자', 'cat': '취미교양', 'tags': ['취미교양', '여행', '국내여행', '식도락'], 'intro': '주말을 이용해 전국의 숨은 명소와 맛집 투어를 떠납니다.', 'pres': '현여행'},
    {'name': 'E-스포츠연구회', 'cat': '취미교양', 'tags': ['취미교양', '게임', '롤', '발로란트'], 'intro': '리그오브레전드, 발로란트 대회 개최와 스크림을 운영합니다.', 'pres': '지게임'},
    {'name': '애니메이션덕후', 'cat': '취미교양', 'tags': ['취미교양', '애니', '서브컬처', '덕질'], 'intro': '인기 애니메이션 감상과 굿즈 정보 교환을 함께 즐기는 모임입니다.', 'pres': '도애니'},
    {'name': '도예공방', 'cat': '취미교양', 'tags': ['취미교양', '도자기', '물레', '핸드메이드'], 'intro': '흙을 반죽하고 물레를 차서 나만의 머그컵과 그릇을 제작합니다.', 'pres': '진도예'},
    {'name': '마술연구회학', 'cat': '취미교양', 'tags': ['취미교양', '마술', '트릭', '클로즈업'], 'intro': '카드 마술, 동전 마술 등 화려한 스테이지 및 스트릿 마술을 배웁니다.', 'pres': '엄마술'},

    # 종교 (10)
    {'name': '기독교연합 CCC', 'cat': '종교', 'tags': ['종교', '기독교', '큐티', '모임'], 'intro': '캠퍼스 내 사랑과 신앙으로 교제하는 기독교 운동 동아리입니다.', 'pres': '이믿음'},
    {'name': '가톨릭학생회', 'cat': '종교', 'tags': ['종교', '천주교', '미사', '기도'], 'intro': '성당 미사 참여와 성경 봉헌, 친목 모임을 갖는 천주교 동아리입니다.', 'pres': '김천주'},
    {'name': '불교학생회', 'cat': '종교', 'tags': ['종교', '불교', '명상', '템플스테이'], 'intro': '마음의 평화를 찾는 참선 명상과 템플스테이를 다녀옵니다.', 'pres': '박불교'},
    {'name': 'IVF 기독학생회', 'cat': '종교', 'tags': ['종교', '기독교', '성경공부', '세계관'], 'intro': '하나님 나라의 복음을 소망하며 기독 지성인으로 성장하는 모임입니다.', 'pres': '최신앙'},
    {'name': 'JOY 선교회', 'cat': '종교', 'tags': ['종교', '기독교', '찬양', '소그룹'], 'intro': '예수님, 이웃, 나를 차례로 사랑하는 기쁨의 공동체입니다.', 'pres': '정기쁨'},
    {'name': '성경탐구회', 'cat': '종교', 'tags': ['종교', '말씀', '성경', '스터디'], 'intro': '성경 말씀 원문 깊이 읽기와 삶 적용을나누는 동아리입니다.', 'pres': '한말씀'},
    {'name': '찬양동아리글로리아', 'cat': '종교', 'tags': ['종교', '찬양', '워십', '악기'], 'intro': '악기 연주와 찬양 보컬로 하나님을 예배하는 집회 모임입니다.', 'pres': '윤찬양'},
    {'name': '기독교동아리 SFC', 'cat': '종교', 'tags': ['종교', '개혁주의', '신앙', '교제'], 'intro': '학원 복음화와 세계 복음화를 가슴 품는 학생신앙운동입니다.', 'pres': '강신앙'},
    {'name': '마음챙김 명상회', 'cat': '종교', 'tags': ['종교', '명상', '마음챙김', '힐링'], 'intro': '특정 종교에 치우치지 않고 힐링과 마음의 평안을 찾는 명상회입니다.', 'pres': '조힐링'},
    {'name': '원불교학생회', 'cat': '종교', 'tags': ['종교', '원불교', '마음공부', '은혜'], 'intro': '일상 속 마음공부와 은혜 나누기를 함께하는 인성 동아리입니다.', 'pres': '임은혜'}
]

print(f"Total clubs to create: {len(clubs_data)}")

created_count = 0
for idx, c in enumerate(clubs_data, 1):
    club_user_id = f"club{random_lower(6)}"
    club_pw = "Club12345!"
    
    # 1. 사용자 생성
    reg_club = req('POST', '/auth/user/register', {
        'userId': club_user_id,
        'password': club_pw,
        'name': c['pres'],
        'phoneNumber': f"010-{random.randint(1000,9999)}-{random.randint(1000,9999)}"
    }, dev_token)
    
    if not reg_club or 'data' not in reg_club:
        print(f"[{idx}/60] Failed to register club user {club_user_id}: {reg_club}", flush=True)
        continue
        
    # 2. 사용자 로그인
    login_club = req('POST', '/auth/user/login', {
        'userId': club_user_id,
        'password': club_pw
    })
    
    ctoken = login_club.get('data', {}).get('accessToken') if login_club and 'data' in login_club and login_club['data'] else None
    if not ctoken:
        print(f"[{idx}/60] Failed to login for club user {club_user_id}", flush=True)
        continue
        
    # 3. 동아리 ID 조회
    my_club = req('POST', '/auth/user/find/club', {}, ctoken)
    club_id = my_club.get('data', {}).get('clubId') if my_club and 'data' in my_club and my_club['data'] else None
    if not club_id:
        print(f"[{idx}/60] Failed to get clubId for {club_user_id}", flush=True)
        continue
        
    # 4. 동아리 정보 업데이트
    info_payload = {
        'name': c['name'],
        'category': c['cat'],
        'division': '중동',
        'tags': c['tags'],
        'introduction': c['intro'],
        'presidentName': c['pres'],
        'presidentPhoneNumber': f"010-{random.randint(1000,9999)}-{random.randint(1000,9999)}",
        'description': {
            'introDescription': f"{c['name']} 소개글: {c['intro']}",
            'activityDescription': f"{c['name']} 활동 내용 및 정기 모임 소개",
            'awards': [f"2025년 동아리 우수 활동상", f"2024년 동아리 경진대회 입상"],
            'idealCandidate': f"{c['cat']}에 관심이 있고 열정 넘치는 신입부원",
            'benefits': "신입부원 웰컴키트 제공, 동아리방 이용 권한, 정기 회식 지원",
            'faqs': [
                {'question': '초보자도 신청 가능한가요?', 'answer': '네! 기초부터 차근차근 알려드리니 부담 없이 지원해주세요.'},
                {'question': '정기 모임 일정은 어떻게 되나요?', 'answer': '매주 목요일 저녁 6시에 정기 모임이 있습니다.'}
            ]
        },
        'socialLinks': {
            'instagram': f"https://instagram.com/{c['name']}_official",
            'youtube': f"https://youtube.com/c/{c['name']}"
        }
    }
    up_res = req('PUT', '/api/club/info', info_payload, ctoken)
    if up_res and 'data' in up_res:
        created_count += 1
        print(f"[{created_count}/60] Created: {c['name']} ({c['cat']}) - ID: {club_id}", flush=True)

# 5. 최종 총 동아리 개수 확인
search_res = req('GET', '/api/club/search/')
total_clubs = search_res.get('data', {}).get('totalCount') if search_res and 'data' in search_res and search_res['data'] else 0
print(f"[Done] Total clubs in DB: {total_clubs}", flush=True)
