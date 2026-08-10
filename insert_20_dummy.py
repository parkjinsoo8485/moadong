import urllib.request
import urllib.error
import json
import random
import string

base_url = 'http://localhost:8080'

def req(method, path, body=None, token=None):
    url = base_url + path
    data = json.dumps(body, ensure_ascii=False).encode('utf-8') if body is not None else None
    headers = {'Content-Type': 'application/json; charset=utf-8'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    r = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(r) as res:
            return json.loads(res.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        err_body = e.read().decode('utf-8')
        print(f'HTTPError {e.code} {method} {path}: {err_body}')
        return {'error': e.code, 'body': err_body}
    except Exception as e:
        print(f'Exception: {e}')
        return None

def random_lower(length=5):
    return ''.join(random.choices(string.ascii_lowercase, k=length))

# 1. 개발자 계정 획득
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
            print(f"Dev account logged in successfully: {dev_id}")
            break

if not dev_token:
    print("Failed to get dev token")
    exit(1)

# 2. 20개의 검증된 동아리 데이터
clubs_20 = [
    {
        'name': '모바일랩',
        'cat': '학술',
        'tags': ['앱개발', '모바일', '플러터'],
        'intro': '모바일 앱 개발 학술 동아리',
        'pres': '김모바일',
        'activity': 'iOS 및 Android 앱 제작 스터디 및 팀 프로젝트',
        'awards': [{'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['앱 개발 공모전 우수상']}],
        'ideal': {'tags': ['열정', '성실'], 'content': '모바일 앱 개발에 열정이 있는 학생'},
        'benefits': '맥북 개발 환경 및 테스트 기기 지원',
        'faqs': [{'question': '초보도 가능한가요?', 'answer': '기초 스터디 진행합니다.'}]
    },
    {
        'name': '딥러닝랩',
        'cat': '학술',
        'tags': ['AI', '딥러닝', '파이썬'],
        'intro': '인공지능과 딥러닝 연구회',
        'pres': '이딥러닝',
        'activity': 'PyTorch 기반 딥러닝 논문 연구 및 모델 구현',
        'awards': [{'year': 2025, 'semesterTerm': 'SECOND', 'achievements': ['AI 해커톤 최우수상']}],
        'ideal': {'tags': ['AI', '탐구'], 'content': '인공지능 기술을 탐구하고 싶은 모든 학부생'},
        'benefits': 'GPU 인프라 지원 및 세미나',
        'faqs': [{'question': '수학을 잘해야 하나요?', 'answer': '기초부터 함께 공부합니다.'}]
    },
    {
        'name': '알고리즘킹',
        'cat': '학술',
        'tags': ['알고리즘', '코테', 'PS'],
        'intro': '코딩테스트 대비 스터디',
        'pres': '박알고',
        'activity': '매주 백준/프로그래머스 코딩테스트 스터디',
        'awards': [{'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['ICPC 본선 진출']}],
        'ideal': {'tags': ['끈기', '노력'], 'content': '꾸준하게 문제 풀이를 진행할 분'},
        'benefits': '문제 풀이 족보 노션 공유',
        'faqs': [{'question': '어떤 언어를 쓰나요?', 'answer': 'C++, Java, Python 자유 선택입니다.'}]
    },
    {
        'name': '웹마스터',
        'cat': '학술',
        'tags': ['웹개발', '리액트', '스프링'],
        'intro': '풀스택 웹 개발 프론트 백엔드',
        'pres': '최웹',
        'activity': 'React, Spring Boot 기반 웹 서비스 개발 프로젝트',
        'awards': [{'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['대학생 웹경진대회 대상']}],
        'ideal': {'tags': ['팀워크', '협업'], 'content': '팀 프로젝트로 실제 웹 서비스를 만들고 싶은 분'},
        'benefits': '클라우드 서버 비용 지원',
        'faqs': [{'question': '프로젝트 기간은?', 'answer': '한 학기 단위로 진행됩니다.'}]
    },
    {
        'name': '시큐리티존',
        'cat': '학술',
        'tags': ['보안', '해킹', 'CTF'],
        'intro': '정보보안 및 해킹 스터디',
        'pres': '정보안',
        'activity': '웹 해킹, 시스템 해킹 분석 및 CTF 대회 참가',
        'awards': [{'year': 2024, 'semesterTerm': 'FIRST', 'achievements': ['화이트햇 CTF 3위']}],
        'ideal': {'tags': ['보안', '집중'], 'content': '정보보안 분야에 관심 깊은 학생'},
        'benefits': '보안 장비 및 스터디룸 지원',
        'faqs': [{'question': '해킹을 전혀 몰라도 되나요?', 'answer': '기초 입문 강좌를 제공합니다.'}]
    },
    {
        'name': '비트앤바이트',
        'cat': '공연',
        'tags': ['밴드', '기타', '보컬'],
        'intro': '신나는 밴드 사운드 동아리',
        'pres': '강밴드',
        'activity': '학기별 정기 공연 및 대학 축제 찬조 공연',
        'awards': [{'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['대학 밴드 페스티벌 1위']}],
        'ideal': {'tags': ['음악', '열정'], 'content': '보컬, 기타, 베이스, 드럼, 키보드 세션'},
        'benefits': '합주실 무료 이용',
        'faqs': [{'question': '악기를 잘 쳐야 하나요?', 'answer': '열정만 있다면 누구든 환영합니다.'}]
    },
    {
        'name': '하모니합창단',
        'cat': '공연',
        'tags': ['합창', '성악', '하모니'],
        'intro': '아름다운 합창 선율 동아리',
        'pres': '조합창',
        'activity': '정기 연주회 및 재능기부 위문 공연',
        'awards': [{'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['전국 대학 합창대회 은상']}],
        'ideal': {'tags': ['배려', '하모니'], 'content': '노래 부르기를 즐기고 목소리를 모으고 싶은 사람'},
        'benefits': '전문 보컬 트레이닝',
        'faqs': [{'question': '오디션이 있나요?', 'answer': '음역대 체크를 위한 간단한 음정 테스트가 있습니다.'}]
    },
    {
        'name': '스트릿그루브',
        'cat': '공연',
        'tags': ['댄스', '힙합', '팝핀'],
        'intro': '스트릿 댄스 힙합 댄스팀',
        'pres': '윤댄스',
        'activity': '스트릿 댄스 버스킹 및 찬조 공연',
        'awards': [{'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['댄스 배틀 챔피언십 우승']}],
        'ideal': {'tags': ['리듬', '자유'], 'content': '춤을 좋아하고 무대를 즐기는 모든 학생'},
        'benefits': '댄스 전용 연습실 보유',
        'faqs': [{'question': '몸치도 가능한가요?', 'answer': '기초 동작부터 친절히 알려드립니다.'}]
    },
    {
        'name': '씨네마클럽',
        'cat': '취미교양',
        'tags': ['영화', '감상', '시네마'],
        'intro': '영화 감상과 토론 동아리',
        'pres': '장영화',
        'activity': '주간 영화 상영회 및 시네마 토론',
        'awards': [],
        'ideal': {'tags': ['감성', '토론'], 'content': '다양한 장르의 영화를 즐기는 마니아'},
        'benefits': '동아리방 대형 스크린 무료 관람',
        'faqs': [{'question': '어떤 영화를 보나요?', 'answer': '부원들의 투표로 선정합니다.'}]
    },
    {
        'name': '보드마니아',
        'cat': '취미교양',
        'tags': ['보드게임', '보드', '친목'],
        'intro': '다양한 보드게임 플레이',
        'pres': '임보드',
        'activity': '전략 및 테마 보드게임 정기 모임',
        'awards': [],
        'ideal': {'tags': ['친목', '전략'], 'content': '보드게임을 사랑하고 친구를 사귀고 싶은 분'},
        'benefits': '100여 종 보드게임 구비',
        'faqs': [{'question': '룰을 몰라도 되나요?', 'answer': '친절히 가르쳐 드립니다.'}]
    },
    {
        'name': '출사피플',
        'cat': '취미교양',
        'tags': ['사진', '카메라', '출사'],
        'intro': '풍경과 인물 사진 출사',
        'pres': '한사진',
        'activity': '월 1회 출사 및 학기말 사진 전시회',
        'awards': [{'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['사진 공모전 입선']}],
        'ideal': {'tags': ['관찰', '기록'], 'content': '사진을 찍고 기록하는 것을 좋아하는 분'},
        'benefits': '스마트폰/DSLR 촬영 팁 전수',
        'faqs': [{'question': '폰카도 되나요?', 'answer': '스마트폰 카메라로도 충분히 즐길 수 있습니다.'}]
    },
    {
        'name': '셰프앤쿠킹',
        'cat': '취미교양',
        'tags': ['요리', '쿠킹', '베이킹'],
        'intro': '맛있는 요리와 베이킹',
        'pres': '오요리',
        'activity': '격주 요리 실습 및 베이킹 워크숍',
        'awards': [],
        'ideal': {'tags': ['정성', '미식'], 'content': '요리와 베이킹을 배우고 싶은 학생'},
        'benefits': '공유 주방 실습 지원',
        'faqs': [{'question': '재료비는?', 'answer': '동아리비에서 상당 부분 지원합니다.'}]
    },
    {
        'name': '볼더링패밀리',
        'cat': '운동',
        'tags': ['클라이밍', '암벽', '운동'],
        'intro': '스포츠 클라이밍 동아리',
        'pres': '서클라',
        'activity': '실내 클라이밍 암장 모임 및 야외 볼더링',
        'awards': [{'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['볼더링 대회 2위']}],
        'ideal': {'tags': ['도전', '체력'], 'content': '체력 증진과 성취감을 원하는 분'},
        'benefits': '암장 일일 이용권 할인',
        'faqs': [{'question': '초보자도 가능한가요?', 'answer': '기초 난이도부터 차근차근 시작합니다.'}]
    },
    {
        'name': '슛돌이FC',
        'cat': '운동',
        'tags': ['축구', '풋살', '운동'],
        'intro': '열정 가득한 축구 풋살팀',
        'pres': '신축구',
        'activity': '주 1회 풋살 경기 및 학과 대항전 참가',
        'awards': [{'year': 2024, 'semesterTerm': 'FIRST', 'achievements': ['총장배 축구 준우승']}],
        'ideal': {'tags': ['협동', '스포츠'], 'content': '축구를 사랑하는 모든 학생'},
        'benefits': '유니폼 지급 및 체육관 대여',
        'faqs': [{'question': '매니저도 모집하나요?', 'answer': '네, 매니저와 선수 모두 환영합니다.'}]
    },
    {
        'name': '셔틀콕에이스',
        'cat': '운동',
        'tags': ['배드민턴', '셔틀콕', '민턴'],
        'intro': '배드민턴 실력 향상 동아리',
        'pres': '권민턴',
        'activity': '주 2회 체육관 정기 모임 및 동아리 내 난타전',
        'awards': [],
        'ideal': {'tags': ['활력', '매너'], 'content': '배드민턴을 즐기고 싶은 남녀 누구나'},
        'benefits': '공용 라켓 및 셔틀콕 제공',
        'faqs': [{'question': '라켓이 없어도 되나요?', 'answer': '대여용 라켓이 구비되어 있습니다.'}]
    },
    {
        'name': '한강러너스',
        'cat': '운동',
        'tags': ['러닝', '마라톤', '러닝크루'],
        'intro': '함께 달리는 러닝 크루',
        'pres': '황러닝',
        'activity': '주 2회 밤 러닝 세션 및 마라톤 대회 참가',
        'awards': [{'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['마라톤 10km 완주']}],
        'ideal': {'tags': ['건강', '지속'], 'content': '건강하게 땀 흘리며 달리고 싶은 부원'},
        'benefits': '러닝 보급품 및 에너지젤 지원',
        'faqs': [{'question': '속도가 빠르면 힘들지 않나요?', 'answer': '페이스별로 조를 나누어 달립니다.'}]
    },
    {
        'name': '햇살봉사단',
        'cat': '봉사',
        'tags': ['봉사', '멘토링', '나눔'],
        'intro': '지역 사회 따뜻한 멘토링',
        'pres': '송봉사',
        'activity': '지역 아동센터 교육 멘토링 및 학습 지도',
        'awards': [{'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['우수 사회봉사 표창']}],
        'ideal': {'tags': ['봉사', '따뜻함'], 'content': '책임감 있고 따뜻한 마음을 지닌 학생'},
        'benefits': '1365 봉사시간 공식 인정',
        'faqs': [{'question': '시간대가 어떻게 되나요?', 'answer': '주말 오전 시간대 위주입니다.'}]
    },
    {
        'name': '그린어스',
        'cat': '봉사',
        'tags': ['환경', '플로깅', '봉사'],
        'intro': '캠퍼스 환경 플로깅 봉사',
        'pres': '류환경',
        'activity': '주 1회 캠퍼스 및 근교 하천 플로깅',
        'awards': [],
        'ideal': {'tags': ['환경', '실천'], 'content': '환경 보호에 관심 있는 분'},
        'benefits': '생분해 봉투 및 플로깅 키트 제공',
        'faqs': [{'question': '어려운 봉사인가요?', 'answer': '가볍게 산책하듯 참여하면 됩니다.'}]
    },
    {
        'name': '밀알선교회',
        'cat': '종교',
        'tags': ['기독교', '신앙', '기도'],
        'intro': '사랑으로 하나되는 기독교',
        'pres': '전신앙',
        'activity': '주간 성경 모임 및 수련회',
        'awards': [],
        'ideal': {'tags': ['사랑', '신앙'], 'content': '신앙적인 교제를 나누고 싶은 대학생'},
        'benefits': '따뜻한 모임과 식사 교제',
        'faqs': [{'question': '종교가 없어도 되나요?', 'answer': '누구나 부담 없이 오실 수 있습니다.'}]
    },
    {
        'name': '마음명상회',
        'cat': '종교',
        'tags': ['불교', '명상', '참선'],
        'intro': '참선과 명상 불교 동아리',
        'pres': '홍명상',
        'activity': '마음 챙김 명상 프로그램 및 템플스테이',
        'awards': [],
        'ideal': {'tags': ['평화', '마음'], 'content': '마음의 휴식과 평화를 얻고 싶은 분'},
        'benefits': '템플스테이 혜택 제공',
        'faqs': [{'question': '명상을 해본 적 없어요', 'answer': '초보부터 친절히 안내합니다.'}]
    }
]

created_success = 0
for idx, c in enumerate(clubs_20, 1):
    club_uid = f"club20{random_lower(5)}"
    club_pw = "Club12345!"
    
    # 1. 동아리 계정 가입
    reg_res = req('POST', '/auth/user/register', {
        'userId': club_uid,
        'password': club_pw,
        'name': c['pres'],
        'phoneNumber': '010-1234-5678'
    }, dev_token)
    
    if not reg_res or 'data' not in reg_res:
        print(f"[{idx}/{len(clubs_20)}] Failed register user for {c['name']}")
        continue
        
    # 2. 동아리 계정 로그인
    login_res = req('POST', '/auth/user/login', {
        'userId': club_uid,
        'password': club_pw
    })
    
    ctoken = login_res.get('data', {}).get('accessToken') if login_res and 'data' in login_res and login_res['data'] else None
    if not ctoken:
        print(f"[{idx}/{len(clubs_20)}] Failed login for {c['name']}")
        continue
        
    # 3. 동아리 ID 조회
    find_res = req('POST', '/auth/user/find/club', {}, ctoken)
    club_id = find_res.get('data', {}).get('clubId') if find_res and 'data' in find_res and find_res['data'] else None
    if not club_id:
        print(f"[{idx}/{len(clubs_20)}] Failed find clubId for {c['name']}")
        continue
        
    # 4. 동아리 정보(Info) 업데이트
    info_body = {
        'name': c['name'],
        'category': c['cat'],
        'division': '중동',
        'tags': c['tags'],
        'introduction': c['intro'],
        'presidentName': c['pres'],
        'presidentPhoneNumber': '010-1234-5678',
        'description': {
            'introDescription': f"{c['name']}에 오신 것을 환영합니다! {c['intro']}",
            'activityDescription': c['activity'],
            'awards': c['awards'],
            'idealCandidate': c['ideal'],
            'benefits': c['benefits'],
            'faqs': c['faqs']
        },
        'socialLinks': {
            'instagram': f"https://instagram.com/{club_uid}",
            'youtube': f"https://youtube.com/@{club_uid}"
        }
    }
    
    up_info = req('PUT', '/api/club/info', info_body, ctoken)
    
    # 5. 모집 정보(Recruitment) 업데이트
    rec_body = {
        'recruitmentStart': '2026-03-01T00:00:00Z',
        'recruitmentEnd': '2026-03-31T23:59:59Z',
        'recruitmentTarget': '전공 무관 1~4학년 재학생',
        'externalApplicationUrl': f"https://forms.google.com/{club_uid}",
        'sendNotification': False
    }
    up_rec = req('PUT', '/api/club/description', rec_body, ctoken)
    
    if up_info and 'data' in up_info:
        created_success += 1
        print(f"[{created_success}/{len(clubs_20)}] 동아리 생성 성공: {c['name']} (ID: {club_id})")
    else:
        print(f"[{idx}/{len(clubs_20)}] Failed info update for {c['name']}: {up_info}")

# 3. 최종 생성 결과 확인
search_all = req('GET', '/api/club/search/')
total_cnt = search_all.get('data', {}).get('totalCount') if search_all and 'data' in search_all and search_all['data'] else 0
print(f"\n==========================================")
print(f"20개 동아리 더미 데이터 입력 완료!")
print(f"새로 등록 성공한 동아리 수: {created_success}개")
print(f"데이터베이스 내 총 동아리 수: {total_cnt}개")
print(f"==========================================")
