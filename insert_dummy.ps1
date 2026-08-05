 = "http://localhost:8080"

function Invoke-ApiRaw {
    param(, , , )
     = @{ "Content-Type" = "application/json; charset=utf-8" }
    if () { ["Authorization"] = "Bearer " }
    try {
         = [System.Text.Encoding]::UTF8.GetBytes()
         = Invoke-RestMethod -Method  -Uri  -Headers  -Body  -ErrorAction Stop
        return 
    } catch {
        Write-Host "[ERROR]  "
        Write-Host .Exception.Message
        return 
    }
}

Write-Host "=== [1] Dev account register ==="
 = '{"userId":"devuser1","password":"Admin1234!","name":"개발자","phoneNumber":"010-1234-5678","secret":"dummy-dev-secret"}'
Invoke-ApiRaw -Method POST -Uri "/auth/dev/register" -JsonStr  | Out-Null

Write-Host "=== [2] Dev login ==="
 = Invoke-ApiRaw -Method POST -Uri "/auth/user/login" -JsonStr '{"userId":"devuser1","password":"Admin1234!"}'
 = .data.accessToken
Write-Host "Dev token: "

if (-not ) { exit 1 }

 = @(
    @{ uid="club001"; name="봉사동아리"; cat="봉사"; t1="봉사"; t2="나눔"; t3="사회"; intro="따뜻한 사회를 만드는 봉사동아리입니다."; pres="김봉사" },
    @{ uid="club002"; name="IT연구회"; cat="학술"; t1="IT"; t2="개발"; t3="코딩"; intro="최신 기술을 함께 연구하는 학술동아리입니다."; pres="이학술" },
    @{ uid="club003"; name="댄스팀"; cat="공연"; t1="댄스"; t2="공연"; t3="무대"; intro="무대 위에서 빛나는 댄스동아리입니다."; pres="박공연" },
    @{ uid="club004"; name="등산클럽"; cat="운동"; t1="등산"; t2="운동"; t3="자연"; intro="자연과 함께하는 등산동아리입니다."; pres="최운동" },
    @{ uid="club005"; name="음악밴드"; cat="공연"; t1="음악"; t2="밴드"; t3="기타"; intro="함께 연주하고 노래하는 밴드동아리입니다."; pres="정음악" },
    @{ uid="club006"; name="사진동아리"; cat="취미교양"; t1="사진"; t2="카메라"; t3="출사"; intro="아름다운 순간을 기록하는 사진동아리입니다."; pres="강사진" }
)

foreach ( in ) {
    Write-Host "--- Club:  ---"
     = "{"userId":"","password":"Club12345!","name":"","phoneNumber":"010-0000-0001"}"
    Invoke-ApiRaw -Method POST -Uri "/auth/user/register" -JsonStr  -Token  | Out-Null

     = "{"userId":"","password":"Club12345!"}"
     = Invoke-ApiRaw -Method POST -Uri "/auth/user/login" -JsonStr 
     = .data.accessToken
    if (-not ) { continue }

     = Invoke-ApiRaw -Method POST -Uri "/auth/user/find/club" -JsonStr '{}' -Token 
     = .data.clubId
    if (-not ) { continue }

     = "{"name":"","category":"","division":"중동","tags":["","",""],"introduction":"","presidentName":"","presidentPhoneNumber":"010-0000-0001","description":{"introDescription":"","activityDescription":" 활동 소개","awards":[],"idealCandidate":null,"benefits":null,"faqs":[]},"socialLinks":{}}"
    Invoke-ApiRaw -Method PUT -Uri "/api/club/info" -JsonStr  -Token  | Out-Null
}

 = Invoke-ApiRaw -Method GET -Uri "/api/club/search/" -JsonStr ""
Write-Host "=== Total clubs in search:  ==="