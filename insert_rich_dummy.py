import urllib.request
import urllib.error
import json
import random
import string
import time

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

# 2. 24개의 풍부한 동아리 더미 데이터
clubs_master = [
    {
        'name': '모아코딩',
        'cat': '학술',
        'tags': ['코딩', '개발'],
        'intro': '함께 배우고 성장하는 코딩',
        'pres': '김코딩',
        'activity': '매주 알고리즘 풀이, 팀 웹/앱 프로젝트 진행 및 하반기 해커톤 참가',
        'awards': [
            {'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['대학생 해커톤 우수상']},
            {'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['오픈소스 프로젝트 대상']}
        ],
        'ideal_tags': ['열정적인 분'],
        'ideal': '열정적으로 코딩을 배우고 나눌 의지가 있는 모든 학생',
        'benefits': '스터디룸 제공, 도서 구매비 지원, 현직자 멘토링 기회',
        'faqs': [
            {'question': '코딩 초보자도 참여할 수 있나요?', 'answer': '네! 기초 스터디반과 프로젝트반으로 나누어 운영하므로 초보자도 환영합니다.'},
            {'question': '정기 모임은 언제인가요?', 'answer': '매주 화요일 저녁 7시에 진행됩니다.'}
        ]
    },
    {
        'name': '알고랩',
        'cat': '학술',
        'tags': ['알고리즘', 'PS'],
        'intro': '코딩테스트 완벽 대비 알고리즘',
        'pres': '이알고',
        'activity': '백준/프로그래머스 문제 풀이 스터디, 모의 코딩테스트 및 코드 리뷰',
        'awards': [
            {'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['ICPC 서울 리전 본선 진출']}
        ],
        'ideal_tags': ['꾸준한 분'],
        'ideal': '코딩테스트 합격을 목표로 꾸준히 문제 풀이를 지속할 수 있는 분',
        'benefits': '알고리즘 강의 자료 공유, 문제 풀이 모범 답안 노션 제공',
        'faqs': [
            {'question': '어떤 언어를 사용하나요?', 'answer': 'C++, Java, Python 등 본인이 편한 언어를 자유롭게 사용합니다.'}
        ]
    },
    {
        'name': '피치하이츠',
        'cat': '공연',
        'tags': ['힙합', '음악'],
        'intro': '비트 위에서 자유롭게 힙합을',
        'pres': '박힙합',
        'activity': '정기 힙합 정기공연, 자작곡 음원 발매 및 믹스테잎 제작',
        'awards': [
            {'year': 2024, 'semesterTerm': 'FIRST', 'achievements': ['전국 대학 힙합 동아리 연합전 2위']}
        ],
        'ideal_tags': ['음악을 사랑하는 분'],
        'ideal': '힙합 문화를 사랑하고 랩, 비트메이킹, 보컬에 관심 있는 누구나',
        'benefits': '동아리 전용 녹음 부스 및 장비 무료 이용',
        'faqs': [
            {'question': '자작곡이 없어도 되나요?', 'answer': '네, 랩이나 보컬 카피 곡으로도 참여 가능합니다.'}
        ]
    },
    {
        'name': '선율오케스트라',
        'cat': '공연',
        'tags': ['오케스트라', '클래식'],
        'intro': '클래식 선율을 만드는 오케스트라',
        'pres': '최선율',
        'activity': '봄/가을 정기 연주회, 지역 사회 재능기부 봉사 연주회',
        'awards': [
            {'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['대학 오케스트라 페스티벌 최우수상']}
        ],
        'ideal_tags': ['악기 연주자'],
        'ideal': '현악기, 관악기, 타악기 연주 경험이 있는 학생',
        'benefits': '악기 보관함 제공, 전문 지휘자 레슨',
        'faqs': [
            {'question': '악기를 직접 가져와야 하나요?', 'answer': '대형 악기는 동아리방 악기를 이용할 수 있습니다.'}
        ]
    },
    {
        'name': '어울림봉사단',
        'cat': '봉사',
        'tags': ['봉사', '멘토링'],
        'intro': '지역 아동과 함께하는 멘토링',
        'pres': '정봉사',
        'activity': '초/중학생 과목 학습 지도, 창의 체험활동 및 주말 나들이 봉사',
        'awards': [
            {'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['보건복지부 장관 표창 우수봉사동아리']}
        ],
        'ideal_tags': ['책임감 있는 분'],
        'ideal': '아이들을 사랑하고 책임감 있게 봉사에 임할 학생',
        'benefits': '1365/VMS 봉사시간 인정, 봉사 수료증 발급',
        'faqs': [
            {'question': '봉사 활동 시간은 언제인가요?', 'answer': '매주 토요일 오전 10시~12시에 진행됩니다.'}
        ]
    },
    {
        'name': '사랑나눔회',
        'cat': '봉사',
        'tags': ['유기동물', '환경'],
        'intro': '유기동물 보호소 봉사 및 플로깅',
        'pres': '강나눔',
        'activity': '월 2회 유기견/유기묘 보호소 견사 청소 및 산책 봉사, 캠퍼스 플로깅',
        'awards': [
            {'year': 2024, 'semesterTerm': 'FIRST', 'achievements': ['유기동물 보호협회 감사패']}
        ],
        'ideal_tags': ['따뜻한 마음'],
        'ideal': '동물과 환경을 사랑하는 따뜻한 마음을 가진 분',
        'benefits': '봉사시간 지급, 보호소 기부 굿즈 제작 기회',
        'faqs': [
            {'question': '동물 알레르기가 있어도 되나요?', 'answer': '보호소 외부 봉사나 굿즈 디자인, 플로깅 활동으로 참여 가능합니다.'}
        ]
    },
    {
        'name': 'FC모아',
        'cat': '운동',
        'tags': ['축구', '풋살'],
        'intro': '땀 흘리며 우정을 나누는 축구',
        'pres': '윤축구',
        'activity': '주 1회 정기 풋살/축구 경기, 대학 대항전 축구 대회 참가',
        'awards': [
            {'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['총장배 축구대회 우승']}
        ],
        'ideal_tags': ['축구 좋아하는 분'],
        'ideal': '축구와 풋살을 즐기는 남녀 매니저 및 선수',
        'benefits': '동아리 유니폼 제공, 체육관 우선 예약',
        'faqs': [
            {'question': '초보자도 경기에 뛸 수 있나요?', 'answer': '체계적인 훈련 체계와 친선 경기가 마련되어 있어 초보자도 환영합니다.'}
        ]
    },
    {
        'name': '스매시',
        'cat': '운동',
        'tags': ['배드민턴', '셔틀콕'],
        'intro': '스릴 넘치는 배드민턴 동아리',
        'pres': '임민턴',
        'activity': '주 2회 체육관 정기 모임, 급수별 자체 대회 및 뒤풀이',
        'awards': [
            {'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['전국 배드민턴 동호인 대회 단식 1위']}
        ],
        'ideal_tags': ['배드민턴 좋아하는 분'],
        'ideal': '배드민턴을 재미있게 치고 싶은 누구나',
        'benefits': '셔틀콕 제공, 라켓 대여 가능',
        'faqs': [
            {'question': '라켓이 없는데 참여할 수 있나요?', 'answer': '동아리 공용 라켓을 대여해 드립니다.'}
        ]
    },
    {
        'name': '서브미션',
        'cat': '운동',
        'tags': ['주짓수', '격투기'],
        'intro': '실전 호신술과 주짓수 트레이닝',
        'pres': '한주짓',
        'activity': '주짓수 기술 연습, 대련(스파링) 및 체력 증진 트레이닝',
        'awards': [
            {'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['전국 대학 주짓수 챔피언십 메달']}
        ],
        'ideal_tags': ['도전 정신 있는 분'],
        'ideal': '건강한 체력과 자기방어 호신술을 배우고 싶은 분',
        'benefits': '도복 공동구매 할인, 주짓수 벨트 심사 기회',
        'faqs': [
            {'question': '부상 위험은 없나요?', 'answer': '안전 수칙을 철저히 준수하며 지도자 동석 하에 연습합니다.'}
        ]
    },
    {
        'name': '렌즈속세상',
        'cat': '취미교양',
        'tags': ['사진', '출사'],
        'intro': '카메라로 담는 아름다운 순간들',
        'pres': '한출사',
        'activity': '월 1회 근교 정기 출사, 카메라 기초 강좌, 학기말 사진 전시회',
        'awards': [
            {'year': 2024, 'semesterTerm': 'SECOND', 'achievements': ['대학생 사진 공모전 입선']}
        ],
        'ideal_tags': ['사진 좋아하는 분'],
        'ideal': '사진 촬영을 좋아하고 여행을 즐기는 모든 분',
        'benefits': '미러리스 사진 팁 전수, 사진집 출판',
        'faqs': [
            {'question': 'DSLR 카메라가 필수인가요?', 'answer': '아닙니다! 스마트폰 카메라만으로도 충분히 참여 가능합니다.'}
        ]
    },
    {
        'name': '보드홀릭',
        'cat': '취미교양',
        'tags': ['보드게임', '친목'],
        'intro': '다양한 전략 보드게임 동아리',
        'pres': '오보드',
        'activity': '매주 동아리방 보드게임 모임, 지니어스 게임 대회',
        'awards': [],
        'ideal_tags': ['친목 좋아하는 분'],
        'ideal': '사람들과 교류하며 보드게임을 즐기고 싶은 분',
        'benefits': '100여 종의 다양한 보드게임 무료 이용',
        'faqs': [
            {'question': '규칙을 몰라도 괜찮나요?', 'answer': '룰마스터 부원들이 친절하게 설명해드립니다.'}
        ]
    },
    {
        'name': '쉐프보나페티',
        'cat': '취미교양',
        'tags': ['요리', '베이킹'],
        'intro': '맛있는 요리와 디저트를 만드는',
        'pres': '서요리',
        'activity': '격주 쿠킹 클래스 및 베이킹, 세계 요리 시식회',
        'awards': [
            {'year': 2024, 'semesterTerm': 'FIRST', 'achievements': ['창의 요리 경연대회 동상']}
        ],
        'ideal_tags': ['요리 좋아하는 분'],
        'ideal': '요리와 베이킹에 관심이 많고 음식을 나누고 싶은 사람',
        'benefits': '공유 주방 대여 지원, 레시피 북 제공',
        'faqs': [
            {'question': '재료비는 어떻게 부담하나요?', 'answer': '동아리비와 약간의 실비로 공동 진행합니다.'}
        ]
    },
    {
        'name': '커피테이스터',
        'cat': '취미교양',
        'tags': ['커피', '드립'],
        'intro': '핸드드립과 커핑을 즐기는 커피',
        'pres': '신커피',
        'activity': '다양한 싱글 오리진 원두 커핑, 에스프레소 추출 연습, 카페 탐방',
        'awards': [
            {'year': 2025, 'semesterTerm': 'FIRST', 'achievements': ['대학생 바리스타 챔피언십 참가']}
        ],
        'ideal_tags': ['커피 좋아하는 분'],
        'ideal': '커피 향미를 즐기고 커피 지식을 넓히고 싶은 부원',
        'benefits': '원두 할인, 핸드드립 도구 세트 사용',
        'faqs': [
            {'question': '카페인에 민감한 사람은 어떤가요?', 'answer': '디카페인 원두 커핑 세션도 마련되어 있습니다.'}
        ]
    },
    {
        'name': 'CCC캠퍼스',
        'cat': '종교',
        'tags': ['기독교', '신앙'],
        'intro': '사랑과 은혜가 넘치는 기독교',
        'pres': '권신앙',
        'activity': '주간 순모임, 채플, 방학 수련회 및 미디어 사역',
        'awards': [],
        'ideal_tags': ['신앙인'],
        'ideal': '신앙을 나누고 건강한 공동체를 경험하고 싶은 학생',
        'benefits': '선배들의 따뜻한 멘토링과 식사 교제',
        'faqs': [
            {'question': '교회를 다니지 않아도 참석 가능한가요?', 'answer': '네, 기독교 문화에 관심 있는 누구나 참여 가능합니다.'}
        ]
    },
    {
        'name': '가톨릭학생회',
        'cat': '종교',
        'tags': ['천주교', '미사'],
        'intro': '주님 안에서 하나 되는 가톨릭',
        'pres': '안미사',
        'activity': '주간 학생 미사, 성경 봉독 및 성지 순례',
        'awards': [],
        'ideal_tags': ['천주교 관심있는 분'],
        'ideal': '가톨릭 신자 또는 천주교에 흥미를 가진 모든 이',
        'benefits': '교구 청년 프로그램 참여 기회',
        'faqs': [
            {'question': '미사는 어디서 드리나요?', 'answer': '인근 성당 및 교목실에서 함께 미사를 드립니다.'}
        ]
    },
    {
        'name': '불교학생회',
        'cat': '종교',
        'tags': ['불교', '참선'],
        'intro': '마음의 평화와 지혜를 찾는 불교',
        'pres': '송참선',
        'activity': '명상/참선 프로그램, 템플스테이 및 불교 철학 세미나',
        'awards': [],
        'ideal_tags': ['마음 안정 원하는 분'],
        'ideal': '바쁜 일상 속 마음의 안정을 찾고 싶은 사람',
        'benefits': '템플스테이 참가비 지원, 명상 공간 제공',
        'faqs': [
            {'question': '명상을 해본 적이 없는데 괜찮나요?', 'answer': '초보자를 위한 호흡법부터 차근차근 안내해 드립니다.'}
        ]
    },
    {
        'name': 'AI인텔리전스',
        'cat': '학술',
        'tags': ['AI', '머신러닝'],
        'intro': '딥러닝과 생성형 AI 기술을 탐구하는 학회',
        'pres': '조인공',
        'activity': 'PyTorch 기반 딥러닝 논문 리딩, 챗봇 및 인공지능 모델 토이 프로젝트',
        'awards': ['2025 AI 챌린지 1위'],
        'ideal': '인공지능 분야로 커리어를 개발하고 싶은 학생',
        'benefits': 'GPU 서버 자원 지원, AI 연구실 멘토링',
        'faqs': [
            {'question': '수학 지식이 많이 필요한가요?', 'answer': '선형대수와 확률 기초부터 함께 스터디합니다.'}
        ]
    },
    {
        'name': '극단모아',
        'cat': '공연',
        'tags': ['연극', '뮤지컬'],
        'intro': '열정과 감동을 선사하는 연극 동아리',
        'pres': '배연극',
        'activity': '연극 발성/연기 워크숍, 정기 공연 연출, 무대 음향/조명 제작',
        'awards': ['전국 대학 연극제 작품상 수상'],
        'ideal': '배우, 연출, 스태프, 조명, 음향 분야에 열정이 넘치는 분',
        'benefits': '소극장 연극 무대 경험, 무대 제작 기기술 습득',
        'faqs': [
            {'question': '연기 경험이 전혀 없어도 배우가 될 수 있나요?', 'answer': '네! 기초 트레이닝 과정을 거쳐 모든 부원이 무대에 설 수 있습니다.'}
        ]
    },
    {
        'name': '모아스트릿',
        'cat': '공연',
        'tags': ['스트릿', '댄스'],
        'intro': '힙합, 팝핀, 락킹, 왁킹 스트릿 댄스 동아리',
        'pres': '문스트릿',
        'activity': '장르별 정기 댄스 세션, 거리 버스킹 및 대학교 축제 공연',
        'awards': ['스트릿 댄스 배틀 대회 퍼포먼스 부문 금상'],
        'ideal': '몸치 탈출부터 댄스 챔피언까지 춤을 즐기고 싶은 분',
        'benefits': '전문 댄스 연습실 무료 사용',
        'faqs': [
            {'question': '연습 일정은 어떻게 되나요?', 'answer': '주 2회 저녁 시간에 장르별 연습이 있습니다.'}
        ]
    },
    {
        'name': '클라임홀릭',
        'cat': '운동',
        'tags': ['클라이밍', '암벽'],
        'intro': '한 단계 한 단계 벽을 넘어가는 스포츠 클라이밍',
        'pres': '양클라',
        'activity': '주 1회 실내 암장 볼더링 모임, 가을 야외 리드 암벽 등반',
        'awards': ['대학생 볼더링 대회 중급부 우승'],
        'ideal': '성취감과 다이어트를 동시에 이루고 싶은 사람',
        'benefits': '암장 일일 이용권 할인, 암벽화 대여',
        'faqs': [
            {'question': '악력이 약해도 할 수 있나요?', 'answer': '클라이밍은 코어와 발 사용이 중요하므로 누구나 즐길 수 있습니다.'}
        ]
    },
    {
        'name': '시네마천국',
        'cat': '취미교양',
        'tags': ['영화', '감상'],
        'intro': '독립영화와 명작 영화를 함께 보고 토론하는 동아리',
        'pres': '추시네',
        'activity': '주간 동아리방 영화 상영회, 단편 영화 제작 워크숍',
        'awards': ['국제 단편 영화제 출품'],
        'ideal': '영화 분석과 영화 감상평 쓰기를 좋아하는 영화 마니아',
        'benefits': '대형 빔프로젝터 영화관 동아리방 보유',
        'faqs': [
            {'question': '어떤 장르의 영화를 보나요?', 'answer': '상업영화부터 클래식, 독립영화까지 부원들의 투표로 선정합니다.'}
        ]
    },
    {
        'name': '글벗토론',
        'cat': '학술',
        'tags': ['독서', '토론'],
        'intro': '책 한 권으로 깊은 독서와 인문학적 토론',
        'pres': '지독서',
        'activity': '격주 독서 토론회, 찬반 의제 토론 배틀, 서평 집필',
        'awards': ['전국 대학생 토론대회 입상'],
        'ideal': '생각의 넓이를 확장하고 논리적 말하기를 기르고 싶은 부원',
        'benefits': '베스트셀러 도서 지원, 논리적 글쓰기 피드백',
        'faqs': [
            {'question': '책을 다 읽지 못하면 토론에 못 오나요?', 'answer': '발제문을 발췌하여 읽고 참여할 수도 있습니다.'}
        ]
    },
    {
        'name': '에코플로깅',
        'cat': '봉사',
        'tags': ['환경', '플로깅'],
        'intro': '조깅하면서 쓰레기를 줍는 에코 웰빙 동아리',
        'pres': '손에코',
        'activity': '주 1회 하천 및 도심 플로깅, 업사이클링 제품 만들기 워크숍',
        'awards': ['지방자치단체 친환경 우수 단체상'],
        'ideal': '건강도 챙기고 지구 환경도 지키고 싶은 미닝아웃 대학생',
        'benefits': '생분해 쓰레기봉투 및 집게 제공, 봉사시간 지급',
        'faqs': [
            {'question': '조깅 속도가 빠른가요?', 'answer': '가볍게 걸으며 쓰레기를 줍는 산책 형태로 진행됩니다.'}
        ]
    },
    {
        'name': '유스챔버',
        'cat': '공연',
        'tags': ['앙상블', '연주'],
        'intro': '소규모 실내악 앙상블 음악 동아리',
        'pres': '노실내',
        'activity': '듀엣/트리오/쿼르텟 앙상블 연습, 병원 및 요양원 위문 연주',
        'awards': ['실내악 앙상블 경연대회 은상'],
        'ideal': '피아노, 바이올린, 플루트 등 악기 연주가 가능한 학생',
        'benefits': '연주회 드레스/턱시도 대여, 악보 라이브러리 제공',
        'faqs': [
            {'question': '합주 팀은 어떻게 구성되나요?', 'answer': '희망 악기와 성향에 맞추어 팀을 구성해 드립니다.'}
        ]
    }
]

created_success = 0
for idx, c in enumerate(clubs_master, 1):
    club_uid = f"club{random_lower(6)}"
    club_pw = "Club12345!"
    
    # 1. 동아리 계정 가입
    reg_res = req('POST', '/auth/user/register', {
        'userId': club_uid,
        'password': club_pw,
        'name': c['pres'],
        'phoneNumber': '010-0000-0001'
    }, dev_token)
    
    if not reg_res or 'data' not in reg_res:
        print(f"[{idx}/{len(clubs_master)}] Failed register user for {c['name']}")
        continue
        
    # 2. 동아리 계정 로그인
    login_res = req('POST', '/auth/user/login', {
        'userId': club_uid,
        'password': club_pw
    })
    
    ctoken = login_res.get('data', {}).get('accessToken') if login_res and 'data' in login_res and login_res['data'] else None
    if not ctoken:
        print(f"[{idx}/{len(clubs_master)}] Failed login for {c['name']}")
        continue
        
    # 3. 동아리 ID 조회
    find_res = req('POST', '/auth/user/find/club', {}, ctoken)
    club_id = find_res.get('data', {}).get('clubId') if find_res and 'data' in find_res and find_res['data'] else None
    if not club_id:
        print(f"[{idx}/{len(clubs_master)}] Failed find clubId for {c['name']}")
        continue
        
    # 4. 동아리 정보(Info) 업데이트
    info_body = {
        'name': c['name'],
        'category': c['cat'],
        'division': '중동',
        'tags': c['tags'],
        'introduction': c['intro'],
        'presidentName': c['pres'],
        'presidentPhoneNumber': '010-0000-0001',
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
        print(f"[{created_success}/{len(clubs_master)}] 동아리 및 모집정보 생성 성공: {c['name']} (ID: {club_id})")

# 3. 최종 생성 결과 확인
search_all = req('GET', '/api/club/search/')
total_cnt = search_all.get('data', {}).get('totalCount') if search_all and 'data' in search_all and search_all['data'] else 0
print(f"\n==========================================")
print(f"대용량 더미 동아리 데이터 입력 완료!")
print(f"새로 등록 성공한 동아리 수: {created_success}개")
print(f"데이터베이스 내 총 동아리 수: {total_cnt}개")
print(f"==========================================")
