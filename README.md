# VaultConsoleExample

HashiCorp Vault의 **Transit Engine**을 활용하여  
암호화/복호화를 수행하는 간단한 Java 콘솔 예제 프로젝트입니다.

---

## 📌 프로젝트 소개

이 프로젝트는 Vault를 활용하여 애플리케이션에서 **암호화 키를 직접 다루지 않고도** 암·복호화를 수행하는 방법을 이해하기 위해 작성되었습니다.

Vault는 HTTP 기반의 REST API를 제공하기 때문에, 별도의 SDK 없이도 일반적인 HTTP 호출만으로 연동이 가능합니다.

---

## 🎯 목적

- Vault Transit Engine 동작 방식 이해
- 암호화 키를 애플리케이션 외부에서 관리하는 구조 학습
- KMS(Key Management System) 대체 가능성 검토
- Java 환경에서 Vault API 직접 호출 실습

---

## 🏗️ 아키텍처 개념

```text
애플리케이션
   ↓ (암호화 요청)
Vault (Transit Engine)
   ↓ (결과 반환)
애플리케이션
```

- 애플리케이션은 평문 데이터를 Vault로 전달
- Vault는 내부 키를 사용해 암호화 수행
- 암호화된 결과만 반환

👉 이 과정에서 **암호화 키는 외부로 노출되지 않습니다**

---

## ⚙️ 실행 환경

- Java 11+
- HashiCorp Vault (dev mode 또는 운영 환경)
- Docker (선택)

---

## 🚀 Vault 실행 (개발용)

```bash
docker run --cap-add=IPC_LOCK -d \
  -p 8200:8200 \
  -e "VAULT_DEV_ROOT_TOKEN_ID=root" \
  -e "VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200" \
  hashicorp/vault
```

---

## 🔧 사전 준비
1. Transit Engine 활성화
vault secrets enable transit
2. 암호화 키 생성
vault write -f transit/keys/test-key

---

**⚠️ 주의사항**

예제에서는 root 토큰을 사용하지만, 실제 환경에서는 사용하지 않는 것이 좋습니다.
JSON 파싱은 문자열 방식으로 구현되어 있으며, 실무에서는 Jackson 등의 라이브러리를 사용하는 것이 권장됩니다.
Vault dev mode는 테스트용이며, 운영 환경에서는 별도의 구성(init/unseal/TLS)이 필요합니다.
