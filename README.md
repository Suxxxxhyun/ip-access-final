[v2는 해당 링크를 통홰 확인 가능](https://github.com/Suxxxxhyun/ip-access-final2)

# IP Access Analyzer

CSV 액세스 로그 파일을 REST API로 분석/요약합니다. 대용량 파일에서도 안정적으로 동작하고, 잘못된 라인이나 외부 API 실패에 견고하며, 필드 확장에 유연하도록 설계했습니다. 결과는 상위 경로/상태코드 비율/접속 IP 등의 통계를 제공합니다.

**Swagger UI**
- `http://localhost:8080/swagger-ui.html`

**핵심 포인트**
- 스트리밍/라인 단위 처리로 전체 파일을 메모리에 올리지 않음.
- 잘못된 로그 라인은 샘플링하고, 나머지는 계속 분석.
- FieldSpec 기반 집계: 필드가 추가되어도 집계 파이프라인 변경 최소화.
- 파일 해시 기반 캐시: 동일 파일 요청 시 기존 결과 반환.
- 외부 IP 정보 조회는 **@Retry 애노테이션** + 타임아웃 설정으로 견고하게 처리.
- 응답에는 분석 결과와 함께 파싱 오류 개수 및 샘플이 포함됨.
- 업로드 파일은 `multipart/form-data`로 전송해야 함.

---

**패키지 구조**
```
com.ipaccessanalyzer
├─ IpAccessAnalyzerApplication.java
├─ application
│  ├─ dto
│  │  ├─ EnrichedIpCount.java
│  │  ├─ IpInfo.java
│  │  ├─ ParsedLog.java
│  │  └─ StatusCode.java
│  ├─ service
│  │  ├─ AnalysisLogService.java
│  │  ├─ AnalysisQueryService.java
│  │  └─ IpEnrichmentService.java
│  └─ usecase
│     ├─ AnalysisLogUseCase.java
│     └─ AnalysisQueryUseCase.java
├─ domain
│  ├─ enums
│  │  └─ FieldSpec.java
│  └─ model
│     ├─ AccessLog.java
│     └─ analysisReport
│        ├─ Report.java
│        ├─ ReportStatistic.java
│        ├─ item
│        │  ├─ BaseItem.java
│        │  ├─ ReportItem.java
│        │  └─ SamplingReportItem.java
│        └─ summary
│           ├─ BaseSummary.java
│           ├─ RankSummary.java
│           └─ RatioSummary.java
├─ exception
│  ├─ AnalysisResultNotFoundException.java
│  ├─ ApiErrorResponse.java
│  ├─ DuplicateAnalysisException.java
│  ├─ ErrorCode.java
│  └─ GlobalExceptionHandler.java
├─ infrastructure
│  ├─ config
│  │  ├─ OpenApiConfig.java
│  │  └─ RestClientConfig.java
│  ├─ ipinfo
│  │  ├─ IpInfoCache.java
│  │  ├─ IpInfoClient.java
│  │  ├─ IpInfoHttpClient.java
│  │  └─ IpInfoResponse.java
│  ├─ parser
│  │  ├─ AccessLogParser.java
│  │  ├─ LogParser.java
│  │  └─ PathNormalizer.java
│  └─ store
│     ├─ AnalysisResultStore.java
│     ├─ InMemoryAnalysisResultStore.java
│     └─ hash
│        └─ FileHashGenerator.java
├─ presentation
│  ├─ AnalysisController.java
│  ├─ dto
│  │  ├─ request
│  │  │  └─ AnalysisRequest.java
│  │  └─ response
│  │     ├─ AnalysisResultResponse.java
│  │     ├─ AnalysisResultStatisticResponse.java
│  │     ├─ IpAccessResponse.java
│  │     └─ StatusCodeStatisticResponse.java
│  └─ validation
│     ├─ NotEmptyFile.java
│     └─ NotEmptyFileValidator.java
└─ support
   └─ ReportContext.java
```

---


**핵심 구조 설명**
- `ReportContext`가 전체 분석 흐름의 상태를 관리합니다.
- 정상 로그는 `ReportContext.accumulate()`로 집계되고, 파싱 실패는 `recordParseError()`로 분리 집계됩니다.
- 정상/에러 모두 `totalCount`에 포함되어 전체 규모를 보존합니다.
- 에러 원문은 `SamplingReportItem`으로 샘플링되며 최대 5건만 보관합니다.

**확장성 설계**
- 도메인(`domain`)은 핵심 규칙과 모델을 보유하고, 애플리케이션(`application`)은 유스케이스를 오케스트레이션합니다.
- 프레젠테이션(`presentation`)과 인프라(`infrastructure`)는 기술 세부사항을 담당하며 도메인에 의존합니다.
- `LogParser` 인터페이스를 통해 CSV 외의 포맷(예: JSON, TSV)으로 파서를 교체할 수 있습니다.
- `AnalysisResultStore`는 저장소 추상화로, 인메모리에서 DB/캐시/파일 기반 저장소로 확장 가능합니다.
- `FieldSpec`는 필드 추가에 따른 집계 확장을 흡수하며, 신규 필드도 동일한 파이프라인으로 처리됩니다.
- IP 정보 조회는 별도 인프라 모듈로 분리되어 외부 API 변경/교체에 유연합니다.
- 해시 기반 캐시는 분석 비용이 큰 파일 처리에 대해 재사용을 보장합니다.

**집계 상속 구조**
- `BaseItem`은 모든 집계 항목의 공통 베이스이며 `FieldSpec`을 통해 필드 설명과 추출 로직을 공유합니다.
- `ReportItem`은 정상 로그의 빈도/비율/순위 집계를 담당합니다.
- `SamplingReportItem`은 실패 원문을 샘플링하는 목적의 집계입니다.

**요약 모델**
- `RankSummary`: 상위 N건 통계를 위한 요약 모델.
- `RatioSummary`: 전체 대비 비율 통계를 위한 요약 모델(상태코드 등).

---

**요청 Endpoint**

1. `POST /analysis`
- 설명: CSV 파일 업로드 분석 요청
- Content-Type: `multipart/form-data`
- 필드명: `file`
- 처리 방식: 헤더 1행을 건너뛴 후, 각 라인을 CSV로 파싱하여 통계 누적
- 결과: 분석 보고서 ID와 파싱 오류 샘플 제공

**응답 예시 (성공)**
```json
{
  "reportId": "2aec0006-5ca3-4c59-9ffa-8576a10b3787",
  "error": {
    "count": 1000,
    "samples": [
      "\"1/29/2026, 5:44:10.000 AM\",121.158.115.86,GET,/event/banner/mir2/popup,MyThreadedApp/1.0,200,HTTP/1.1,176,1138,0,TLSv1.2,/event/banner/mir2/popup",
      "\"1/29/2026, 5:44:10.000 AM\",61.38.42.234,GET,/bbs/list/mir2free,python-requests/2.25.1,200,HTTP/1.1,167,6023,0,TLSv1.3,/bbs/list/mir2free"
    ]
  }
}
```
- 파싱 오류가 없으면 `error`는 `null`입니다.
- 동일 파일이 다시 업로드되면 파일 해시 기반 캐시 결과를 반환합니다.
- 캐시는 파일 내용 기준으로 동작합니다(파일명과 무관).
- 파싱 실패 샘플은 최대 5건만 포함됩니다.

2. `GET /analysis/{reportId}`
- 설명: reportId로 분석 통계 조회
- 결과: 상위 경로, 상태코드 비율, IP Top 목록을 반환

**응답 예시 (성공)**
```json
{
  "totalCount": 12345,
  "topPaths": [
    { "key": "/index", "count": 1000 }
  ],
  "topStatusCodes": [
    { "errorCode": "OK", "ratio": 0.95 },
    { "errorCode": "BAD_REQUEST", "ratio": 0.05 }
  ],
  "topIps": [
    {
      "ipInfo": { "ip": "1.2.3.4", "city": "Seoul" },
      "count": 50
    }
  ]
}
```
- `ratio`는 전체 요청 수 대비 비율입니다.
- IP 정보는 외부 API 조회 실패 시 `ipInfo`가 `null`일 수 있습니다.

---

**신뢰성과 확장성**
- **오류 라인**: 파싱 실패는 카운트/샘플링(`error.count`, `error.samples`)하고, 정상 라인은 계속 처리.
- **빈 파일**: 업로드 검증으로 차단하며, 예외는 안전하게 처리.
- **대용량 파일**: 라인 단위 스트리밍으로 메모리 폭주 방지.
- **정확성**: 상태코드 비율은 전체 건수를 분모로 계산.
- **외부 API 실패**: IP 조회는 **@Retry 애노테이션**과 타임아웃으로 복구를 시도하며, 실패 시에도 분석 흐름은 유지.
- **응답 일관성**: 오류가 있어도 보고서 ID와 분석 결과는 항상 반환되도록 설계.

---

**확장성**
- `FieldSpec`에 필드를 추가하면, 서비스 로직 변경 없이 자동 집계됩니다.
- `ReportItem`은 필드별 집계를 담당하므로 새로운 집계 규칙도 쉽게 확장할 수 있습니다.
