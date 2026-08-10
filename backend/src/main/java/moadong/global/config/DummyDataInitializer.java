package moadong.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moadong.club.entity.Club;
import moadong.club.enums.ClubCategory;
import moadong.club.enums.ClubDivision;
import moadong.club.enums.SemesterTerm;
import moadong.club.payload.dto.ClubAwardDto;
import moadong.club.payload.dto.ClubDescriptionDto;
import moadong.club.payload.dto.ClubIdealCandidateDto;
import moadong.club.payload.dto.FaqDto;
import moadong.club.payload.request.ClubInfoRequest;
import moadong.club.payload.request.ClubRecruitmentInfoUpdateRequest;
import moadong.user.payload.request.DevRegisterRequest;
import moadong.user.payload.request.UserRegisterRequest;
import moadong.club.repository.ClubRepository;
import moadong.club.util.RecruitmentStateCalculator;
import moadong.user.entity.User;
import moadong.user.repository.UserRepository;
import moadong.user.service.UserCommandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataInitializer implements CommandLineRunner {

    private final UserCommandService userCommandService;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    private record ClubSeed(
            String uid,
            String name,
            ClubCategory cat,
            String pres,
            List<String> tags,
            String intro,
            String activity,
            List<ClubAwardDto> awards,
            ClubIdealCandidateDto idealCandidate,
            String benefits,
            List<FaqDto> faqs
    ) {}

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting 60 rich dummy clubs initialization...");

        try {
            // 1. 개발자 계정 생성
            if (userRepository.findUserByUserId("devadmin").isEmpty()) {
                DevRegisterRequest devReq = new DevRegisterRequest("devadmin", "Admin1234!", "개발자", "010-1234-5678", "dummy-dev-secret");
                userCommandService.registerDeveloper(devReq);
                log.info("Dev account created: devadmin");
            }

            // 2. 60개 풍부한 동아리 데이터 목록 (intro <= 24자, tags <= 3개, 각 tag <= 5자)
            List<ClubSeed> seeds = List.of(
                // 봉사 (10)
                new ClubSeed("club001", "봉사동아리", ClubCategory.봉사, "김봉사", List.of("봉사", "나눔", "사회"), "따뜻한 사회를 만드는 봉사동아리",
                        "주 1회 지역 사회복지관 방문 학습지도 및 주말 유기동물 보호소 정기 봉사",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("우수 봉사동아리 보건복지부 표창"))),
                        new ClubIdealCandidateDto(List.of("따뜻한 마음", "책임감"), "봉사와 성실함으로 이웃에 나눔을 실천하고 싶은 모든 학생"),
                        "1365/VMS 봉사시간 공식 인정, 봉사 활동 수료증 발급",
                        List.of(new FaqDto("초보자도 신청 가능한가요?", "네! 신입 부원을 위한 오리엔테이션과 사전 교육이 준비되어 있습니다."))),
                new ClubSeed("club007", "해바라기봉사단", ClubCategory.봉사, "나아동", List.of("봉사", "아동", "돌봄"), "아동센터 학습 보조 봉사단",
                        "지역 아동센터 초등학생 맞춤형 교과목 지도 및 문화 체험 활동",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("지역 아동 복지 감사패"))),
                        new ClubIdealCandidateDto(List.of("아동사랑", "인내심"), "아이들을 사랑하고 지속적으로 멘토링에 참여할 수 있는 분"),
                        "봉사활동 실적 등록 및 우수 멘토 장학금 추천",
                        List.of(new FaqDto("봉사 주기는 어떻게 되나요?", "매주 토요일 오전 10시부터 2시간 동안 진행됩니다."))),
                new ClubSeed("club008", "유기동물보호회", ClubCategory.봉사, "동보호", List.of("봉사", "유기견", "보호소"), "유기동물 정기 봉사활동 동아리",
                        "보호소 견사 청소, 유기견 산책 및 SNS 입양 홍보 콘텐츠 제작",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("생명존중 봉사 대상"))),
                        new ClubIdealCandidateDto(List.of("동물사랑", "활동성"), "유기동물에 애정을 가지고 정기 봉사에 동참할 학생"),
                        "동물보호 봉사시간 인정 및 기부 굿즈 수령",
                        List.of(new FaqDto("동물 알레르기가 있으면 힘들까요?", "보호소 외부 플로깅 및 입양 홍보 팀으로 활동하실 수 있습니다."))),
                new ClubSeed("club009", "환경정화단", ClubCategory.봉사, "송환경", List.of("봉사", "플로깅", "환경"), "해변 플로깅 환경 보호 봉사단",
                        "부산 인근 해수욕장 및 캠퍼스 주변 플로깅, 업사이클링 캠페인",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("환경보호", "열정"), "지구 환경 보호와 플로깅 활동에 관심이 높은 학생"),
                        "플로깅 키트 무료 제공 및 봉사 시간 부여",
                        List.of(new FaqDto("준비물이 필요한가요?", "집게와 생분해 봉투는 동아리에서 지원합니다."))),
                new ClubSeed("club010", "교육봉사단멘토", ClubCategory.봉사, "류멘토", List.of("봉사", "멘토링", "청소년"), "청소년 학습 멘토링 봉사단",
                        "소외계층 청소년 대상 진로 상담 및 기초 학력 향상 멘토링",
                        List.of(new ClubAwardDto(2024, SemesterTerm.FIRST, List.of("교육격차 해소 우수 멘토단"))),
                        new ClubIdealCandidateDto(List.of("지식나눔", "성실"), "청소년의 꿈을 응원하고 체계적으로 학업을 도울 수 있는 분"),
                        "교육봉사 시간 이수 및 교직 이수자 우대",
                        List.of(new FaqDto("멘토링 과목은 직접 선택하나요?", "본인이 자신 있는 과목(국/영/수/코딩 등)을 선택할 수 있습니다."))),
                new ClubSeed("club011", "사랑의연탄나눔", ClubCategory.봉사, "차나눔", List.of("봉사", "연탄", "독거노인"), "독거노인 연탄 생필품 지원 나눔단",
                        "동절기 저소득층 가구 연탄 배달 및 독거노인 안부 확인 생필품 전달",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("체력", "봉사심"), "추운 겨울 이웃에게 온기를 전달할 따뜻한 이웃사랑을 지닌 분"),
                        "봉사활동 인증서 발급",
                        List.of(new FaqDto("겨울에만 활동하나요?", "봄/여름에는 도시락 배달 및 안부 전화 봉사를 진행합니다."))),
                new ClubSeed("club012", "벽화봉사단디자인", ClubCategory.봉사, "신벽화", List.of("봉사", "벽화", "미술"), "골목길 알록달록 벽화 그려요",
                        "노후된 낙후 지역 골목길 및 아동 시설 벽화 디자인 작업",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("도시재생 환경 개선 공로상"))),
                        new ClubIdealCandidateDto(List.of("금손", "협동심"), "미술을 좋아하거나 벽화 채색 활동에 열정이 있는 누구나"),
                        "미술 재료 무료 제공 및 단체 봉사 스펙",
                        List.of(new FaqDto("미술 전공자만 가능한가요?", "밑그림에 맞춰 채색하는 작업이 많아 비전공자도 가능합니다."))),
                new ClubSeed("club013", "다문화지원단", ClubCategory.봉사, "백문화", List.of("봉사", "다문화", "한국어"), "다문화 가정 한국어 교육 지원단",
                        "다문화 가정 아동 한국어 공부 및 이주 여성 적응 지원 프로그램",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("글로벌", "친절"), "다문화 사회 이해도가 높고 친절하게 한국어를 설명할 수 있는 부원"),
                        "한국어 교원 실습 및 봉사 인증",
                        List.of(new FaqDto("외국어 능력이 필수인가요?", "한국어 교육 위주이므로 한국어만 잘 하셔도 충분합니다."))),
                new ClubSeed("club014", "헌혈사랑봉사단", ClubCategory.봉사, "홍헌혈", List.of("봉사", "헌혈", "기부"), "정기 헌혈 생명 나눔 봉사단",
                        "캠퍼스 헌혈차 유치 홍보 캠페인 및 정기 혈액 기부 나눔",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("대한적십자사 표창"))),
                        new ClubIdealCandidateDto(List.of("생명나눔", "적극성"), "헌혈에 대한 긍정적인 인식을 전파할 대학생"),
                        "헌혈 기념품 및 봉사시간 추가 적립",
                        List.of(new FaqDto("헌혈을 못 하는 조건이면 참가 못 하나요?", "캠페인 기획 및 헌혈의 집 안내 봉사로 참여할 수 있습니다."))),
                new ClubSeed("club015", "재능기부음악단", ClubCategory.봉사, "서위로", List.of("봉사", "음악", "재능기부"), "요양원 방문 음악 위로 봉사단",
                        "지역 요양원 및 사회복지 시설 방문 악기 연주 및 합창 위문 공연",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("음악사랑", "감성"), "보컬이나 악기 연주 재능으로 이웃에게 기쁨을 주고 싶은 부원"),
                        "공연 봉사 스펙 및 음향 장비 사용",
                        List.of(new FaqDto("어떤 음악을 연주하나요?", "어르신들이 좋아하는 트로트, 가곡부터 클래식까지 다양합니다."))),

                // 학술 (10)
                new ClubSeed("club002", "IT연구회", ClubCategory.학술, "이학술", List.of("IT", "개발", "코딩"), "최신 기술을 연구하는 학술동아리",
                        "웹/앱 풀스택 프로젝트 개발, 코드 리뷰, 하반기 학술제 전시",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("대학생 SW 경진대회 최우수상"))),
                        new ClubIdealCandidateDto(List.of("열정적인 분", "성장의지"), "개발자로서의 성장을 위해 꾸준히 프로젝트를 완수할 부원"),
                        "서버 자원 지원, 기술 서적 지원, 현직자 멘토링",
                        List.of(new FaqDto("초보자도 스터디 따라갈 수 있나요?", "기초 스터디와 프로젝트 스터디를 수준별로 분리하여 진행합니다."))),
                new ClubSeed("club016", "AI데이터랩", ClubCategory.학술, "김인공", List.of("학술", "AI", "빅데이터"), "인공지능 데이터 분석 학술동아리",
                        "머신러닝/딥러닝 논문 연구, 데이콘/캐글 경진대회 팀 참가",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("DACON AI 챌린지 2위"))),
                        new ClubIdealCandidateDto(List.of("수학적사고", "끈기"), "인공지능 모델 설계와 데이터 분석에 흥미를 느끼는 분"),
                        "고성능 GPU 클라우드 인프라 지원",
                        List.of(new FaqDto("파이썬을 배워본 적 없는데 가능한가요?", "학기 초 파이썬 기초 워크숍을 제공합니다."))),
                new ClubSeed("club017", "알고리즘마스터", ClubCategory.학술, "박알고", List.of("학술", "코테", "알고리즘"), "코딩 테스트 알고리즘 스터디",
                        "백준/프로그래머스 골드 이상 문제 풀이 스터디, 모의 코딩테스트",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("ICPC Korea 본선 진출"))),
                        new ClubIdealCandidateDto(List.of("문제해결", "꾸준함"), "대기업 코딩테스트 합격을 목표로 1일 1재출을 실천할 분"),
                        "알고리즘 문제 모범 답안 족보 노션을 제공합니다.",
                        List.of(new FaqDto("사용하는 주 언어는 무엇인가요?", "C++, Java, Python을 주로 사용합니다."))),
                new ClubSeed("club018", "로봇공학회", ClubCategory.학술, "최로봇", List.of("학술", "로봇", "하드웨어"), "나만의 로봇을 제작하는 학술동아리",
                        "아두이노/라즈베리파이 기반 임베디드 로봇 설계 및 3D 프린팅 쉘 제작",
                        List.of(new ClubAwardDto(2024, SemesterTerm.FIRST, List.of("대한민국 로봇경진대회 동상"))),
                        new ClubIdealCandidateDto(List.of("하드웨어", "메이커"), "직접 하드웨어와 소프트웨어를 결합하여 만들어보고 싶은 학생"),
                        "동아리 전용 3D 프린터 및 납땜/전자 도구 무상 이용",
                        List.of(new FaqDto("전자회로 지식이 없어도 되나요?", "기초 모듈 연결부터 친절하게 안내합니다."))),
                new ClubSeed("club019", "경제경영연구회", ClubCategory.학술, "정경영", List.of("학술", "경제", "경영"), "글로벌 경제 경영 연구 학술동아리",
                        "글로벌 마켓 케이스 스터디, 기업 전략 분석 및 마케팅 공모전 출전",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("전국 대학생 경영전략 공모전 대상"))),
                        new ClubIdealCandidateDto(List.of("분석력", "논리력"), "기업 경영 트렌드와 경제 현상 분석에 매력을 느끼는 분"),
                        "경영학 학술지 구독권 및 공모전 상금 지원",
                        List.of(new FaqDto("상경계열 학생만 모집하나요?", "아닙니다! 다양한 전공의 부원들이 시너지를 내고 있습니다."))),
                new ClubSeed("club020", "금융투자학회", ClubCategory.학술, "한금융", List.of("학술", "주식", "금융"), "모의 주식 투자 연구 학술동아리",
                        "주식/채권/퀀트 투자 전략 리포트 작성 및 실전 모의투자 대회",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("증권사 대학생 모의투자 우수상"))),
                        new ClubIdealCandidateDto(List.of("금융지식", "통찰력"), "재무제표 분석 및 자산 배분 전략에 관심이 깊은 부원"),
                        "금융권 취업 현직 선배 멘토링 혜택",
                        List.of(new FaqDto("투자를 한 번도 안 해봤어도 되나요?", "기초 차트 분석 및 재무분석부터 체계적으로 배웁니다."))),
                new ClubSeed("club021", "스타트업랩", ClubCategory.학술, "윤창업", List.of("학술", "창업", "스타트업"), "창업 경진대회 준비 학술동아리",
                        "아이템 발굴, 비즈니스 모델(BM) 수립, 정부지원사업 IR 피칭 준비",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("예비창업패키지 최종 선정"))),
                        new ClubIdealCandidateDto(List.of("도전정신", "실행력"), "자신의 아이디어로 사업화를 꿈꾸는 예비 창업가"),
                        "시제품 제작 지원금 및 법인 설립 전문가 상담 지원",
                        List.of(new FaqDto("개발자가 없어도 창업팀 구성이 가능한가요?", "기획자, 디자이너, 개발자를 팀 빌딩해 드립니다."))),
                new ClubSeed("club022", "천문관측회", ClubCategory.학술, "강우주", List.of("학술", "천문", "관측"), "밤하늘 별자리 관측 학술동아리",
                        "천체망원경 작동법 학습, 분기별 지방 정기 관측 출사",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("우주사랑", "낭만"), "밤하늘 별과 우주의 신비에 빠져들고 싶은 분"),
                        "고성능 천체망원경 및 카메라 장비 대여",
                        List.of(new FaqDto("관측 일정은 보통 언제인가요?", "달의 맑은 정도에 맞춰 월 1~2회 금요일 밤에 진행됩니다."))),
                new ClubSeed("club023", "역사탐구회", ClubCategory.학술, "조역사", List.of("학술", "역사", "답사"), "유적 답사와 역사 연구 학술동아리",
                        "역사적 사건 토론 및 분기별 전국 문화재 역사 유적 답사",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("인문학", "답사"), "역사 속 이야기를 깊이 있게 이해하고 탐구하고 싶은 분"),
                        "역사 답사 기획 및 답사비 일부 지원",
                        List.of(new FaqDto("답사는 어디로 가나요?", "경주, 공주, 부여, 서울 등 주요 역사 유적지로 떠납니다."))),
                new ClubSeed("club024", "생명과학연구회", ClubCategory.학술, "임바이오", List.of("학술", "바이오", "실험"), "바이오 기술 탐구 학술동아리",
                        "유전자 재조합 기초 실험, 바이오 헬스케어 트렌드 세미나",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("생명공학 학술포럼 입선"))),
                        new ClubIdealCandidateDto(List.of("실험정신", "탐구력"), "생명과학과 제약/바이오산업 트렌드 연구에 열정이 있는 누구나"),
                        "실험실 도구 지원 및 바이오 기업 견학",
                        List.of(new FaqDto("비전공자도 실험 참여가 가능한가요?", "안전 교육 완료 후 기본 분자생물학 실험에 참여하실 수 있습니다."))),

                // 공연 (10)
                new ClubSeed("club003", "댄스팀", ClubCategory.공연, "박공연", List.of("댄스", "공연", "무대"), "무대 위에서 빛나는 댄스동아리",
                        "K-POP 커버 댄스, 힙합/왁킹 안무 연출, 정기 버스킹 및 대학교 축제 무대",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("전국 대학 댄스 경연대회 대상"))),
                        new ClubIdealCandidateDto(List.of("열정", "끼"), "무대 위에서 마음껏 에너지와 끼를 발산하고 싶은 분"),
                        "전문 댄스 연습실 사용 및 공연 의상 지원",
                        List.of(new FaqDto("춤을 잘 못 춰도 무대에 설 수 있나요?", "파트별 기초 안무 연습을 통해 누구나 무대에 설 수 있습니다."))),
                new ClubSeed("club005", "음악밴드", ClubCategory.공연, "정음악", List.of("음악", "밴드", "기타"), "함께 연주하는 밴드동아리",
                        "인디 록, 자작곡 합주, 봄/가을 대학가 라이브 클럽 정기 공연",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("대학 가요제 은상"))),
                        new ClubIdealCandidateDto(List.of("합주", "열정"), "보컬, 세션(기타/베이스/드럼/키보드)으로 음악을 만들 학생"),
                        "동아리 합주실 및 최신 음향 악기 무상 이용",
                        List.of(new FaqDto("세션 지원 시 오디션이 있나요?", "간단한 일체형 곡 합주로 부담 없는 실력 체크를 진행합니다."))),
                new ClubSeed("club025", "스트릿댄스크루", ClubCategory.공연, "오스트릿", List.of("공연", "힙합", "팝핀"), "힙합 스트릿 댄스 전문 공연팀",
                        "팝핀, 락킹, 브레이킹 서클 배틀 및 Street Dance 버스킹",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("스트릿 배틀 루키 클래스 1위"))),
                        new ClubIdealCandidateDto(List.of("스트릿", "자유"), "스트릿 댄스 문화와 리듬을 사랑하는 멋쟁이 부원"),
                        "개인 연습 공간 보장 및 단체 티셔츠 제공",
                        List.of(new FaqDto("장르를 정해서 들어와야 하나요?", "입부 후 기초 워크숍을 듣고 장르를 선택하셔도 됩니다."))),
                new ClubSeed("club026", "통기타울림", ClubCategory.공연, "황통기타", List.of("공연", "통기타", "어쿠스틱"), "감성 통기타 보컬 공연동아리",
                        "어쿠스틱 통기타 코드 연주 및 핑거스타일, 소규모 카페 라이브 버스킹",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("감성", "어쿠스틱"), "통기타 하나로 감동적인 멜로디를 노래하고 싶은 부원"),
                        "동아리 통기타 무상 대여",
                        List.of(new FaqDto("악보를 읽을 줄 몰라도 되나요?", "코드 타브 악보로 쉽게 가르쳐 드립니다."))),
                new ClubSeed("club027", "극단무대", ClubCategory.공연, "문연극", List.of("공연", "연극", "연기"), "직접 연극 무대를 올리는 동아리",
                        "연기 발성 연습, 대본 리딩, 연간 2회 정기 소극장 연극 공연",
                        List.of(new ClubAwardDto(2024, SemesterTerm.FIRST, List.of("대학 연극제 최우수 연기상"))),
                        new ClubIdealCandidateDto(List.of("연기력", "몰입"), "배우, 연출, 조명, 음향, 무대 디자인에 도전할 열정 인재"),
                        "전문 소극장 대관 및 연극 티켓 수입 혜택",
                        List.of(new FaqDto("연기 경험이 전혀 없는데요?", "기초 연기 발성 워크숍 과정을 거치니 걱정 마세요."))),
                new ClubSeed("club028", "뮤지컬컴퍼니", ClubCategory.공연, "양뮤지컬", List.of("공연", "뮤지컬", "노래"), "명작 뮤지컬 무대를 만드는 동아리",
                        "유명 뮤지컬 갈라쇼 및 창작 뮤지컬 정기 공연 기획",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("전국 대학 뮤지컬 페스티벌 우수상"))),
                        new ClubIdealCandidateDto(List.of("가창력", "퍼포먼스"), "노래, 연기, 무용을 종합적으로 선사하고 싶은 뮤지컬 인재"),
                        "전문 앙상블 보컬 트레이닝 지원",
                        List.of(new FaqDto("무대 스태프도 모집하나요?", "음향, 무대 연출, 의상 스태프도 함께 모집합니다."))),
                new ClubSeed("club029", "아카펠라하모니", ClubCategory.공연, "변화음", List.of("공연", "아카펠라", "보컬"), "목소리 화음 아카펠라 공연동아리",
                        "반주 없이 목소리로 만드는 아름다운 5성부 아카펠라 합창",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("화음", "귀여운보컬"), "음감이 뛰어나고 사람들과 목소리를 맞추기 좋아하는 부원"),
                        "녹음실 마이크 음향 장비 지원",
                        List.of(new FaqDto("어떤 파트를 모집하나요?", "소프라노, 알토, 테너, 베이스, 비트박서 모두 모집합니다."))),
                new ClubSeed("club030", "풍물패신명", ClubCategory.공연, "하전통", List.of("공연", "풍물", "사물놀이"), "신명 나는 전통 사물놀이 동아리",
                        "꽹과리, 장구, 북, 징 사물놀이 기락 연습 및 캠퍼스 대동제 길놀이",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("전국 대학 풍물 경연대회 금상"))),
                        new ClubIdealCandidateDto(List.of("전통", "신명"), "우리 국악 장단과 신명나는 흥에 함께 빠져들 인재"),
                        "전통 국악기 무료 제공 및 대동제 메인 공연",
                        List.of(new FaqDto("악기 소리가 너무 크지 않나요?", "방음이 완비된 야외 연습 공간에서 안전하게 연습합니다."))),
                new ClubSeed("club031", "재즈앙상블", ClubCategory.공연, "고재즈", List.of("공연", "재즈", "피아노"), "재즈 곡을 합주하는 공연동아리",
                        "스탠다드 재즈, 보사노바 스윙 피아노/색소폰/트럼펫 즉흥 연주",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("재즈감성", "즉흥연주"), "자유로운 리듬과 재즈 화성에 매료된 연주자"),
                        "재즈 악보집 및 정기 연주 대관 지원",
                        List.of(new FaqDto("즉흥 연주(Improvisation)를 못해도 되나요?", "기본 스케일 스터디를 함께 진행하므로 괜찮습니다."))),
                new ClubSeed("club032", "힙합클럽비트", ClubCategory.공연, "남힙합", List.of("공연", "힙합", "비트"), "비트제작과 랩 가사 만드는 동아리",
                        "자작곡 비트메이킹, 믹스테잎 음원 발매 및 힙합 연합 정기공연",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("대학 힙합 루키 믹스테잎 1위"))),
                        new ClubIdealCandidateDto(List.of("비트메이킹", "라임"), "자신만의 스토리로 랩 가사를 쓰고 음원을 발매할 리얼 래퍼"),
                        "개인 녹음 부스 및 레코딩 마이크 이용",
                        List.of(new FaqDto("비트 찍는 미디 프로그램을 몰라요", "작곡 프로그램(Cubase/Logic/Ableton) 기초 강좌가 제공됩니다."))),

                // 운동 (10)
                new ClubSeed("club004", "등산클럽", ClubCategory.운동, "최운동", List.of("등산", "운동", "자연"), "자연과 함께하는 등산동아리",
                        "월 2회 전국 주요 명산(금정산, 지리산, 덕유산 등) 트레킹 및 야외 캠핑",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("강철체력", "자연사랑"), "맑은 공기를 마시며 성취감 있는 등산을 즐길 분"),
                        "등산 전용 배낭 및 안전 장비 대여 지원",
                        List.of(new FaqDto("등산화가 없어도 참여 가능한가요?", "초반에는 편안한 운동화로도 가능한 코스로 안내합니다."))),
                new ClubSeed("club033", "풋살매니아", ClubCategory.운동, "노풋살", List.of("운동", "풋살", "축구"), "매주 정기 풋살 경기 운동동아리",
                        "주 1회 정기 풋살 경기, 지역 대학 리그전 참가 및 자체 챔피언스리그",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("지역 대학 풋살 대회 우승"))),
                        new ClubIdealCandidateDto(List.of("팀워크", "풋살"), "매너 있고 즐겁게 공을 차며 스트레스를 날릴 부원"),
                        "풋살장 구장 대여료 100% 동아리 지원",
                        List.of(new FaqDto("여성 부원도 경기 참여가 되나요?", "혼성 match 및 여성 풋살 세션을 함께 운영 중입니다."))),
                new ClubSeed("club034", "농구동아리슬램", ClubCategory.운동, "하농구", List.of("운동", "농구", "코트"), "스피디한 농구 매치 운동동아리",
                        "주 2회 체육관 실내 코트 농구 훈련 및 자체 5on5, 3on3 스파링",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("총장배 농구대회 준우승"))),
                        new ClubIdealCandidateDto(List.of("슬램덩크", "스피드"), "농구 코트에서 열정을 불태울 선수 및 매니저"),
                        "실내 농구 코트 및 단체 유니폼 지급",
                        List.of(new FaqDto("포지션 지정이 있나요?", "자유자재로 훈련 후 본인이 원하는 포지션을 맞춰갑니다."))),
                new ClubSeed("club035", "테니스아카데미", ClubCategory.운동, "곽테니스", List.of("운동", "테니스", "라켓"), "테니스 초보부터 즐기는 동아리",
                        "테니스 기본 서브/포핸드 코칭, 월 정기 테니스 코트 매치",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("전국 신인 테니스 대회 3위"))),
                        new ClubIdealCandidateDto(List.of("라켓", "테린이"), "테니스를 기초부터 체계적으로 배우고 싶은 대학생"),
                        "테니스 공 및 고급 테니스 라켓 무료 대여",
                        List.of(new FaqDto("레슨 비용이 따로 드나요?", "선배들의 재능기부 코칭으로 별도 코칭비가 없습니다."))),
                new ClubSeed("club036", "배드민턴클럽", ClubCategory.운동, "성민턴", List.of("운동", "배드민턴", "셔틀콕"), "쉽고 신나는 배드민턴 운동동아리",
                        "급수별(A/B/C/D) 복식 매치 훈련, 동아리 내부 셔틀콕 배틀",
                        List.of(new ClubAwardDto(2024, SemesterTerm.FIRST, List.of("대학 배드민턴 연합전 단식 1위"))),
                        new ClubIdealCandidateDto(List.of("순발력", "셔틀콕"), "빠른 템포의 스매싱으로 체력을 증진하고픈 분"),
                        "삼나무 코트 및 최고급 셔틀콕 무제한 제공",
                        List.of(new FaqDto("체육관 이용 시간이 언제인가요?", "매주 화/목 저녁 6시~8시에 진행됩니다."))),
                new ClubSeed("club037", "클라이밍크루", ClubCategory.운동, "배암벽", List.of("운동", "볼더링", "암벽등반"), "실내 볼더링 암벽등반 운동동아리",
                        "실내 암장 볼더링 난이도 파란색/빨간색 깨기, 야외 리드 클라이밍",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("대학생 볼더링 페스티벌 2위"))),
                        new ClubIdealCandidateDto(List.of("코어힘", "도전"), "벽을 오르며 한 문제씩 문제를 풀어가는 성취감을 느낄 분"),
                        "인근 암장 회원권 할인 혜택 및 초크백 지원",
                        List.of(new FaqDto("팔힘이 약한데도 괜찮나요?", "볼더링은 발 위치와 밸런스가 중요하여 누구나 가능합니다."))),
                new ClubSeed("club038", "수영사랑", ClubCategory.운동, "전수영", List.of("운동", "수영", "체력"), "정기 수영 함께하는 운동동아리",
                        "자유형/평영/접영 영법 교정 훈련 및 수중 피트니스",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("수영", "체력"), "물속에서 전신 운동으로 건강하고 탄탄한 체력을 만드실 분"),
                        "실내 수영장 강습 혜택",
                        List.of(new FaqDto("물을 무서워하는데 수영 배울 수 있나요?", "초급반 키판 잡기부터 차근차근 안내합니다."))),
                new ClubSeed("club039", "탁구교실", ClubCategory.운동, "유탁구", List.of("운동", "탁구", "핑퐁"), "빠른 템포의 스매시 탁구 동아리",
                        "드라이브/커트 드라이브 훈련, 동아리방 탁구대 정기 단식/복식 대회",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("핑퐁", "민첩"), "날카로운 스매싱과 랠리를 실내에서 즐기고 싶은 부원"),
                        "탁구대 및 탁구채 무상 이용",
                        List.of(new FaqDto("공용 탁구채가 준비되어 있나요?", "펜홀더, 셰이크핸드 라켓 모두 구비되어 있습니다."))),
                new ClubSeed("club040", "러닝크루", ClubCategory.운동, "심러닝", List.of("운동", "러닝", "조깅"), "공원을 달리는 페이스 러닝 크루",
                        "주 2회 5km/10km 시티 러닝, 마라톤 대회 참가",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("부산 마라톤 하프 완주 단체상"))),
                        new ClubIdealCandidateDto(List.of("러닝", "자기관리"), "자신의 페이스대로 함께 달리며 러너스 하이를 맛볼 부원"),
                        "러닝 에너지바 제공 및 마라톤 참가비 지원",
                        List.of(new FaqDto("달리기 속도가 느려도 같이 뛸 수 있나요?", "페이스 그룹(5분대, 6분대, 7분대)을 나누어 달립니다."))),
                new ClubSeed("club041", "볼링클럽", ClubCategory.운동, "주볼링", List.of("운동", "볼링", "스트라이크"), "스트라이크 쾌감 나누는 볼링동아리",
                        "자세 교정 정기전, 핀 훅구 / 에버리지 향상 모임",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("볼링장 연합전 최고 에버리지 우승"))),
                        new ClubIdealCandidateDto(List.of("스트라이크", "멘탈"), "시원한 스트라이크로 일상 스트레스를 풀고 싶은 대학생"),
                        "볼링장 게임비 할인가 적용 및 개인 공 수건 지원",
                        List.of(new FaqDto("마이볼이 없어도 참여 가능한가요?", "하우스볼로 충분히 정기전에 참가 가능합니다."))),

                // 취미교양 (10)
                new ClubSeed("club006", "사진동아리", ClubCategory.취미교양, "강사진", List.of("사진", "카메라", "출사"), "순간을 기록하는 사진동아리",
                        "주말 계절별 출사, 필름/디지털 사진 보정 강좌, 연말 갤러리 사진 전시회",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("대한민국 대학생 사진 공모전 입선"))),
                        new ClubIdealCandidateDto(List.of("감성", "기록"), "아름다운 순간과 인물/풍경을 렌즈에 담고 싶은 모든 분"),
                        "카메라 바디/렌즈 무상 대여 및 사진집 출판",
                        List.of(new FaqDto("스마트폰 카메라로만 찍어도 되나요?", "네! 폰카메라 보정법 스터디도 활발합니다."))),
                new ClubSeed("club042", "보드게임연구회", ClubCategory.취미교양, "원보드", List.of("보드게임", "전략", "친목"), "전략 보드게임 다양하게 즐기는 곳",
                        "스플렌더, 카탄, 뱅, 훌라 등 100여종 보드게임 모임 및 자체 토너먼트",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("보드게임", "친목"), "두뇌 전략 플레이와 사람들과의 대화를 좋아하는 부원"),
                        "동아리방 내 100종 이상의 최신 보드게임 구비",
                        List.of(new FaqDto("룰을 아예 모르는 보드게임이어도 괜찮나요?", "룰마스터가 플레이 전 친절하게 룰을 설명합니다."))),
                new ClubSeed("club043", "베이킹클럽", ClubCategory.취미교양, "천디저트", List.of("디저트", "쿠키", "제빵"), "디저트와 빵을 구워 나누는 동아리",
                        "쿠키, 마카롱, 소금빵, 에그타르트 직접 제빵 및 시식회",
                        List.of(new ClubAwardDto(2024, SemesterTerm.FIRST, List.of("홈베이킹 디자인 디저트전 동상"))),
                        new ClubIdealCandidateDto(List.of("베이킹", "달콤함"), "달콤한 디저트를 직접 만들어 친구들과 나누고 싶은 분"),
                        "오븐 및 제빵 도구 사용 지원",
                        List.of(new FaqDto("재료비는 어떻게 부담하나요?", "동아리비에서 상당 부분 지원하며 소정의 실비만 냅니다."))),
                new ClubSeed("club044", "영화비평회", ClubCategory.취미교양, "방영화", List.of("영화", "시네마", "토론"), "명작 영화 관람 토론 취미동아리",
                        "주간 영화 상영회, 영화 평론 스터디, 독립영화제 단체 관람",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("영화덕후", "시네필"), "다양한 영화 장르를 깊이 있게 감상하고 이야기를 나누고픈 부원"),
                        "동아리방 빔프로젝터 영화관 이용 및 영화관 티켓 할인가 지원",
                        List.of(new FaqDto("상업영화 위주로만 보나요?", "부원 투표로 고전영화, 애니, 상업영화 등 자유롭게 결정합니다."))),
                new ClubSeed("club045", "캘리그라피손글씨", ClubCategory.취미교양, "공글씨", List.of("손글씨", "붓펜", "감성"), "예쁜 글귀 손글씨 작품 동아리",
                        "붓펜/딥펜 캘리그라피 연습, 감성 글귀 엽서 및 텀블러 제작",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("손글씨 캘리그라피 공모전 우수작"))),
                        new ClubIdealCandidateDto(List.of("금손", "정성"), "자신만의 손글씨체를 교정하고 예쁜 작품을 만들고 싶은 분"),
                        "캘리그라피 붓펜 및 잉크 세트 무상 지급",
                        List.of(new FaqDto("악필이어도 캘리그라피 배울 수 있나요?", "악필 교정부터 붓펜 잡는 법까지 체계적으로 가르쳐 드립니다."))),
                new ClubSeed("club046", "여행크루떠나자", ClubCategory.취미교양, "현여행", List.of("여행", "맛집", "식도락"), "전국 명소 맛집 투어 여행 크루",
                        "월 1회 근교 당일치기 기차 여행, 방학 전국 투어 및 식도락 탐방",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("여행러", "식도락"), "새로운 장소 여행과 맛있는 음식을 먹으며 힐링할 분"),
                        "여행 숙소 할인 혜택 및 단체 여행 사진 앨범 제작",
                        List.of(new FaqDto("여행 비용은 얼마나 드나요?", "가성비 알뜰 여행 코스로 최소 비용을 지향합니다."))),
                new ClubSeed("club047", "E스포츠연구회", ClubCategory.취미교양, "지게임", List.of("게임", "롤", "발로란트"), "롤 발로란트 대회 개최 게임동아리",
                        "League of Legends, Valorant 동아리 내 내전 및 대학교 e스포츠 연합전",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("전국 대학 e스포츠 대항전 LoL 1위"))),
                        new ClubIdealCandidateDto(List.of("티어업", "게이머"), "전략적 게임 플레이와 매너 있는 게임 팀워크를 가진 부원"),
                        "게이밍 기어 협찬 혜택 및 뷰잉 파티 진행",
                        List.of(new FaqDto("티어 제한이 있나요?", "언랭크부터 천상계까지 모든 티어가 참여할 수 있습니다."))),
                new ClubSeed("club048", "애니메이션연구회", ClubCategory.취미교양, "도애니", List.of("애니", "덕질", "굿즈"), "인기 애니 감상 굿즈 교환 동아리",
                        "신작 애니메이션 같이보기, 캐릭터 일러스트 스터디, 애니 서브컬처 탐구",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("덕질", "취향존중"), "자신이 사랑하는 작품과 캐릭터를 마음껏 이야기할 수 있는 공간"),
                        "대형 피규어/만화책 라이브러리 이용",
                        List.of(new FaqDto("코스프레도 하나요?", "원하시는 부원은 서브컬처 행사 코스프레 및 전시에 참여할 수 있습니다."))),
                new ClubSeed("club049", "도예공방", ClubCategory.취미교양, "진도예", List.of("도자기", "물레", "공예"), "나만의 머그컵 그릇 만드는 동아리",
                        "손물레 흙 빚기, 가마 굽기, 도자기 그릇 및 인테리어 오브제 제작",
                        List.of(new ClubAwardDto(2025, SemesterTerm.FIRST, List.of("대학생 핸드메이드 공예전 입선"))),
                        new ClubIdealCandidateDto(List.of("손재주", "몰입"), "흙을 만지며 마음의 안정을 찾고 실용 도자기를 만들 부원"),
                        "도예 흙 점토 및 가마 소성비 지원",
                        List.of(new FaqDto("만든 도자기는 직접 가져가나요?", "가마에서 완결 소성 후 개인 소장이 가능합니다."))),
                new ClubSeed("club050", "마술연구회", ClubCategory.취미교양, "엄마술", List.of("마술", "트릭", "카드"), "스트릿 카드 마술 연구 동아리",
                        "카드 클로즈업 마술, 동전 기술 학습, 길거리 버스킹 마술 공연",
                        List.of(new ClubAwardDto(2024, SemesterTerm.SECOND, List.of("스트릿 마술 콘테스트 최우수상"))),
                        new ClubIdealCandidateDto(List.of("신기함", "화술"), "상대방에게 놀라움과 즐거움을 전하는 마술 연출가가 되고픈 누구나"),
                        "바이시클 고급 마술 카드 무상 제공",
                        List.of(new FaqDto("손기술이 서툴러도 가능한가요?", "기초 미스디렉션과 손재주 훈련으로 금방 터득할 수 있습니다."))),

                // 종교 (10)
                new ClubSeed("club051", "기독교연합CCC", ClubCategory.종교, "이믿음", List.of("종교", "기독교", "큐티"), "신앙으로 모이는 기독교 동아리",
                        "주간 말씀 묵상 순모임, 채플 예배, 여름/겨울 신앙 수련회",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("신앙인", "사랑"), "하나님의 사랑을 알고 대학 생활 속 건강한 영적 성장을 원하는 청년"),
                        "따뜻한 멘토링 선배 교제 및 영적 휴식 공간",
                        List.of(new FaqDto("기독교를 안 믿는데 가도 되나요?", "누구나 편안하게 와서 이야기 나누고 교제할 수 있습니다."))),
                new ClubSeed("club052", "가톨릭학생회", ClubCategory.종교, "김천주", List.of("종교", "천주교", "미사"), "성당 미사 신앙 모임 천주교 동아리",
                        "주간 청년 미사 참석, 성당 성지 순례 및 나눔 기공 활동",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("천주교", "평화"), "가톨릭 신앙 안에서 말씀과 은총을나누고픈 학생"),
                        "교구 청년 캠프 참가비 지원",
                        List.of(new FaqDto("세례를 안 받은 성당 관심자도 되나요?", "네! 성당 문화와 미사에 관심 있는 모든 이들을 환영합니다."))),
                new ClubSeed("club053", "불교학생회", ClubCategory.종교, "박불교", List.of("종교", "불교", "명상"), "참선 명상과 템플스테이 불교 동아리",
                        "참선 명상 실습, 산사 템플스테이 경험 및 불교 경전 인문학 스터디",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("마음평화", "명상"), "학업 스트레스 속 참된 마음의 평화와 자아를 찾고 싶은 청년"),
                        "전국 산사 템플스테이 참가비 100% 지원",
                        List.of(new FaqDto("명상이 처음인데 잘 할 수 있을까요?", "전문 스님의 지도로 차근차근 호흡 명상을 익힙니다."))),
                new ClubSeed("club054", "IVF기독학생회", ClubCategory.종교, "최신앙", List.of("종교", "기독교", "성경공부"), "성경 공부와 삶의 나눔 기독 동아리",
                        "소그룹 성경 소통 모임, 복음적 시각으로 사회 현상 읽기 스터디",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("복음", "공동체"), "캠퍼스와 세상 속에서 세상의 빛이 되는 삶을 고민하는 대학생"),
                        "신앙 기독 서적 무상 제공",
                        List.of(new FaqDto("모임은 얼마나 자주 하나요?", "주 1회 소그룹 모임과 전체 조모임으로 진행됩니다."))),
                new ClubSeed("club055", "JOY선교회", ClubCategory.종교, "정기쁨", List.of("종교", "기독교", "찬양"), "기쁨의 신앙 공동체 종교 동아리",
                        "찬양과 기도로 열어가는 주간 모임, 사랑의 공동체 교제",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("기쁨", "찬양"), "찬양의 기쁨 속에서 진실된 공동체 사랑을 나누고 싶은 이"),
                        "악기 보컬 찬양 훈련 혜택",
                        List.of(new FaqDto("찬양팀 세션도 뽑나요?", "기타, 드럼, 신디, 보컬 세션 모두 환영합니다."))),
                new ClubSeed("club056", "성경탐구회", ClubCategory.종교, "한말씀", List.of("종교", "말씀", "성경"), "성경 읽고 적용하는 종교 동아리",
                        "신구약 성경 연속 통독 스터디, 삶으로 올바르게 적용하기 토론",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("말씀탐구", "진리"), "성경의 진리를 깊이 파헤치고 실천해보고 싶은 부원"),
                        "성경 주석서 연구 자료 제공",
                        List.of(new FaqDto("성경을 읽어본 적이 없는데 어려운가요?", "쉬운 번역본 성경으로 기초부터 탐구해갑니다."))),
                new ClubSeed("club057", "찬양동아리글로리아", ClubCategory.종교, "윤찬양", List.of("종교", "찬양", "워십"), "악기와 보컬 예배 찬양 동아리",
                        "캠퍼스 버스킹 찬양 집회, 워십 댄스 및 정기 찬양 콘서트",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("워십", "음악예배"), "음악과 춤으로 영광스러운 찬양을 드리고자 하는 부원"),
                        "최신 음향 앰프 장비 및 합주 공간 이용",
                        List.of(new FaqDto("워십팀 무용도 같이 배우나요?", "네, 보컬 세션과 워십무용팀이 함께 연출합니다."))),
                new ClubSeed("club058", "SFC학생신앙운동", ClubCategory.종교, "강신앙", List.of("종교", "개혁주의", "교제"), "학생 신앙 운동 종교 동아리",
                        "개혁주의 신앙 개요 스터디, 영적 도전 학원 복음화 운동",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("신앙운동", "비전"), "하나님 나라 가치를 캠퍼스에서 실천할 학생"),
                        "전국 SFC 전국대회 참가 지원",
                        List.of(new FaqDto("SFC 동아리방은 어디에 있나요?", "학생회관 내 기독교 동아리실을 같이 사용합니다."))),
                new ClubSeed("club059", "마음챙김명상회", ClubCategory.종교, "조힐링", List.of("종교", "명상", "힐링"), "힐링 평안 마음챙김 명상 동아리",
                        "마음챙김(Mindfulness) 싱잉볼 힐링, 아로마 차 명상 및 수면 릴렉스",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("마음챙김", "힐링"), "지친 일상 속 번아웃을 극복하고 내면의 평화를 얻고 싶은 청년"),
                        "싱잉볼 및 고급 오가닉 티 무상 음용",
                        List.of(new FaqDto("특정 종교 색채가 강한가요?", "종교에 상관없이 누구나 참여할 수 있는 힐링 명상 모임입니다."))),
                new ClubSeed("club060", "원불교학생회", ClubCategory.종교, "임은혜", List.of("종교", "원불교", "마음공부"), "마음공부와 은혜 나눔 원불교 동아리",
                        "일기 작성 마음공부 스터디, 원불교 교리 이해 및 지역 봉사",
                        List.of(),
                        new ClubIdealCandidateDto(List.of("마음공부", "은혜"), "원만하고 조화로운 마음공부와 은혜 나눔을 함께할 부원"),
                        "원불교 청년 정기 캠프 참여 기회",
                        List.of(new FaqDto("원불교에 대해 잘 몰라도 되나요?", "마음 일기 쓰기부터 차근차근 함께 공유해갑니다.")))
            );

            int createdCount = 0;
            for (ClubSeed seed : seeds) {
                if (createOrUpdateClubDummy(seed)) {
                    createdCount++;
                }
            }

            log.info("Dummy data initialization completed successfully! Processed: {}, Total in DB: {}", createdCount, clubRepository.count());
        } catch (Exception e) {
            log.error("Failed to initialize dummy data: {}", e.getMessage(), e);
        }
    }

    private boolean createOrUpdateClubDummy(ClubSeed seed) {
        User user = userRepository.findUserByUserId(seed.uid()).orElseGet(() -> {
            UserRegisterRequest regReq = new UserRegisterRequest(seed.uid(), "Club1234!", seed.pres(), "010-0000-0001");
            return userCommandService.registerUser(regReq);
        });

        Club club = clubRepository.findClubByUserId(user.getId()).orElse(null);
        if (club == null) {
            log.warn("Club not found for userId: {}", user.getId());
            return false;
        }

        ClubDescriptionDto descDto = new ClubDescriptionDto(
                seed.name() + "에 오신 것을 환영합니다! " + seed.intro(),
                seed.activity(),
                seed.awards(),
                seed.idealCandidate(),
                seed.benefits(),
                seed.faqs()
        );

        Map<String, String> socialLinks = Map.of(
                "instagram", "https://instagram.com/" + seed.uid(),
                "youtube", "https://youtube.com/@" + seed.uid()
        );

        ClubInfoRequest infoReq = new ClubInfoRequest(
                seed.name(),
                seed.cat(),
                ClubDivision.중동,
                seed.tags(),
                seed.intro(),
                seed.pres(),
                descDto,
                "010-0000-0001",
                socialLinks
        );

        club.update(infoReq);

        Instant start = Instant.parse("2026-03-01T00:00:00Z");
        Instant end = Instant.parse("2026-12-31T23:59:59Z");
        ClubRecruitmentInfoUpdateRequest recReq = new ClubRecruitmentInfoUpdateRequest(
                start,
                end,
                "전공 무관 1~4학년 재학생",
                "https://forms.google.com/" + seed.uid(),
                false
        );
        club.update(recReq);
        club.updateRecruitmentStatus(RecruitmentStateCalculator.calculateRecruitmentStatus(
                club.getClubRecruitmentInformation().getRecruitmentStart(),
                club.getClubRecruitmentInformation().getRecruitmentEnd()
        ));

        clubRepository.save(club);
        log.info("Club created/updated: {} ({})", seed.name(), seed.cat());
        return true;
    }
}

