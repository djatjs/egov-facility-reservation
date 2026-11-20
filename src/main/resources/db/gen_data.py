"""
샘플 데이터 생성기 → data.sql

    python src/main/resources/db/gen_data.py

- 날짜는 SQL 실행 시점(SYSDATE) 기준 상대값으로 만든다. 언제 적재해도 "지난 30일 ~ 앞으로 30일" 데이터가 된다.
- 업무 규칙을 지킨다: 같은 시설·날짜에 유효 예약(신청·승인·이용완료) 시간이 겹치지 않음,
  1인 1일 유효 예약 합계 120분 이하, 운영시간 안·예약 단위에 맞춤, 정원 이하.
- 상태와 이력이 맞물린다: 지난 날짜는 이용완료·취소·반려, 오늘은 승인·취소, 앞으로는 신청·승인·반려·취소.
- 같은 시드(SEED)면 같은 결과가 나온다.
"""
import os
import random

SEED = 20261121
DAYS_BACK, DAYS_AHEAD = 30, 28
LIMIT_MIN = 120
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'data.sql')

ADMIN_HASH = '$2a$10$AZEFWpxqTqKf8K6WrUGgEeCPfRyIzt5keaEsiNEaL9wj6q.8mZ9n6'   # Admin1234!
CITIZEN_HASH = '$2a$10$cVlgnccqZV7LCBlfQxYRku7gE94WYOfZaVVNEAgT4FNBSYAv6nYZS'  # Citizen1234!

rnd = random.Random(SEED)

# (유형, 이름, 주소, 정원, 시작, 종료, 단위, 설명, 사용여부)
FCLTIES = [
    ('01', '시민체육관 다목적 코트', '시청로 10 시민체육관 1층', 40, '0900', '2200', 60, '배드민턴 6면, 농구 1면으로 사용할 수 있습니다.', 'Y'),
    ('01', '시민체육관 탁구장', '시청로 10 시민체육관 2층', 16, '0900', '2100', 60, '탁구대 8대가 있습니다. 라켓은 개인 지참입니다.', 'Y'),
    ('01', '동부 국민체육센터 체육관', '동부로 45 국민체육센터', 60, '0600', '2200', 120, '배구·농구 겸용 코트입니다.', 'Y'),
    ('01', '청소년수련관 다목적실', '청년로 8 청소년수련관 3층', 30, '1000', '2000', 60, '댄스·요가 등 실내 활동에 적합합니다.', 'Y'),
    ('02', '주민센터 3층 소회의실', '중앙로 25 주민센터 3층', 12, '0900', '1800', 60, '빔프로젝터와 화이트보드가 있습니다.', 'Y'),
    ('02', '평생학습관 세미나실', '학습로 3 평생학습관 2층', 30, '0900', '2100', 30, '30분 단위로 예약할 수 있습니다.', 'Y'),
    ('02', '시립도서관 그룹스터디룸 A', '책마을로 12 시립도서관 1층', 6, '0900', '2200', 60, '6인용 스터디룸입니다.', 'Y'),
    ('02', '시립도서관 그룹스터디룸 B', '책마을로 12 시립도서관 1층', 8, '0900', '2200', 60, '8인용 스터디룸입니다.', 'Y'),
    ('02', '창업지원센터 회의실', '혁신로 77 창업지원센터 5층', 20, '0900', '1800', 30, '화상회의 장비가 있습니다.', 'Y'),
    ('03', '문화회관 소강당', '문화로 7 문화회관 지하 1층', 150, '1000', '2200', 120, '음향 장비 사용은 사전 문의하세요.', 'Y'),
    ('03', '시청 대회의실', '시청로 1 시청 별관 2층', 200, '0900', '1800', 120, '공공기관·단체 행사 우선입니다.', 'Y'),
    ('03', '여성회관 강당', '여성로 20 여성회관 4층', 100, '0900', '2100', 120, '무대와 피아노가 있습니다.', 'Y'),
    ('04', '강변 인조잔디 운동장', '강변로 100', 60, '0600', '2200', 120, '축구·풋살 겸용 운동장입니다.', 'Y'),
    ('04', '근린공원 풋살장', '공원로 33 근린공원', 14, '0700', '2200', 60, '야간 조명이 있습니다.', 'Y'),
    ('04', '시민공원 테니스장 1번 코트', '시민공원로 5', 4, '0600', '2100', 60, '하드 코트입니다.', 'Y'),
    ('04', '구 시민운동장 보조구장', '운동장로 2', 40, '0800', '1800', 120, '시설 보수로 신규 예약을 받지 않습니다.', 'N'),
]

SURNAMES = list('김이박최정강조윤장임한오서신권황안송류홍')
GIVEN = ['민준', '서연', '도윤', '하은', '지호', '수아', '예준', '지우', '현우', '서윤', '건우', '채원',
         '우진', '지민', '선우', '다은', '연우', '하린', '준서', '유나', '태윤', '소율', '시우', '예린']

PURPOSES = {
    '01': ['배드민턴 동호회 정기 모임', '주말 농구 모임', '직장인 배구 동호회', '어린이 체육 교실', '생활체육 탁구 연습', '요가 소모임'],
    '02': ['아파트 입주자 대표 회의', '스터디 모임', '창업 동아리 회의', '독서 모임', '봉사단 회의', '학부모 모임'],
    '03': ['주민 합창단 발표회', '작은 음악회', '자치회 정기 총회', '청소년 동아리 공연', '교육 설명회'],
    '04': ['조기 축구', '풋살 친선 경기', '유소년 축구 교실', '테니스 레슨', '직장 체육대회 연습'],
}
RETURN_RSN = ['해당 시간에 시설 점검이 예정되어 있습니다.', '같은 시간 기관 행사가 우선 배정되었습니다.',
              '이용 목적이 시설 용도와 맞지 않습니다.', '신청 인원이 시설 안전 기준을 넘습니다.',
              '정기 대관 일정과 겹칩니다.']

NOTICES = [
    ('공공시설 예약 이용 안내', '예약은 이용일 하루 전부터 30일 이내까지 신청할 수 있으며, 관리자 승인 후 확정됩니다.\n1인 1일 최대 2시간까지 예약할 수 있습니다.'),
    ('예약 취소 규정', '승인된 예약도 이용일 전날까지 직접 취소할 수 있습니다.\n당일 취소는 시설 관리자에게 문의하세요.'),
    ('구 시민운동장 보조구장 보수 공사 안내', '시설 보수로 당분간 신규 예약을 받지 않습니다. 공사가 끝나면 다시 안내하겠습니다.'),
    ('시립도서관 그룹스터디룸 운영 시간 변경', '그룹스터디룸 운영 시간이 09:00~22:00으로 늘어났습니다.'),
    ('겨울철 야외 시설 이용 유의사항', '결빙 시 야외 운동장 이용이 제한될 수 있습니다. 이용 전 공지를 확인하세요.'),
    ('개인정보 처리방침 개정 안내', '예약 신청 시 수집하는 개인정보 항목과 보유 기간을 정비했습니다.'),
    ('평생학습관 세미나실 장비 교체', '세미나실 빔프로젝터와 음향 장비를 새로 교체했습니다.'),
    ('시민체육관 탁구대 추가 설치', '탁구장에 탁구대 2대를 추가로 설치했습니다.'),
    ('예약 시스템 점검 안내', '매월 첫째 주 일요일 새벽 2시부터 4시까지 시스템 점검이 있습니다.'),
    ('단체 대관 우선 배정 기준', '공공기관 및 지역 단체 행사는 우선 배정될 수 있습니다.'),
    ('노쇼 방지 협조 요청', '이용하지 않는 예약은 미리 취소해 다른 시민이 이용할 수 있도록 협조 부탁드립니다.'),
    ('문화회관 소강당 음향 장비 사용 안내', '음향 장비가 필요하면 신청 시 사용 목적에 함께 적어 주세요.'),
    ('근린공원 풋살장 야간 조명 운영', '풋살장 야간 조명을 22시까지 운영합니다.'),
]


def q(s):
    return "NULL" if s is None else "'" + s.replace("'", "''") + "'"


def hhmi(m):
    return f'{m // 60:02d}{m % 60:02d}'


def ts(off):
    """SYSDATE 기준 상대 시각(일 단위, 소수 허용)"""
    return f"SYSDATE + ({off:.4f})"


def de(d):
    return f"TO_CHAR(TRUNC(SYSDATE) + ({d}), 'YYYYMMDD')"


# ---------- 회원 ----------
members = [('admin', ADMIN_HASH, '시설관리자', 'ROLE_ADMIN'),
           ('citizen1', CITIZEN_HASH, '김시민', 'ROLE_USER'),
           ('citizen2', CITIZEN_HASH, '이주민', 'ROLE_USER')]
used_names = {'김시민', '이주민', '시설관리자'}
for i in range(3, 31):
    while True:
        nm = rnd.choice(SURNAMES) + rnd.choice(GIVEN)
        if nm not in used_names:
            used_names.add(nm)
            break
    members.append((f'user{i:02d}', CITIZEN_HASH, nm, 'ROLE_USER'))
citizens = [m[0] for m in members if m[3] == 'ROLE_USER']

# ---------- 예약 ----------
occupied = {}      # (시설번호, 날짜) -> [(시작, 종료)]  유효 예약
used_min = {}      # (회원, 날짜) -> 분                    유효 예약
resves, hists = [], []


def free(f, d, b, e):
    return all(not (b < x2 and e > x1) for x1, x2 in occupied.get((f, d), []))


def pick_status(d):
    r = rnd.random()
    if d < 0:
        return '05' if r < 0.75 else ('04' if r < 0.88 else '03')
    if d == 0:
        return '02'  # 당일 취소는 규칙상 불가
    if d <= 2:
        return '02' if r < 0.55 else ('01' if r < 0.80 else ('04' if r < 0.92 else '03'))
    return '01' if r < 0.40 else ('02' if r < 0.75 else ('04' if r < 0.90 else '03'))


for d in range(-DAYS_BACK, DAYS_AHEAD + 1):
    for fi, (ty, nm, _, cap, ob, oe, unit, _, use) in enumerate(FCLTIES, start=1):
        if use == 'N' and d >= 0:
            continue
        open_m = int(ob[:2]) * 60 + int(ob[2:])
        close_m = int(oe[:2]) * 60 + int(oe[2:])
        weekend = (d % 7) in (0, 6)
        tries = rnd.choice([0, 1, 1, 2, 2, 3, 4]) + (2 if weekend else 0)
        for _ in range(tries):
            dur = unit * rnd.choice([1, 1, 2]) if unit < 120 else unit
            dur = min(dur, LIMIT_MIN)
            slots = list(range(open_m, close_m - dur + 1, unit))
            if not slots:
                continue
            b = rnd.choice(slots)
            e = b + dur
            mber = rnd.choice(citizens)
            sttus = pick_status(d)
            active = sttus in ('01', '02', '05')
            if active and (not free(fi, d, b, e) or used_min.get((mber, d), 0) + dur > LIMIT_MIN):
                continue
            if active:
                occupied.setdefault((fi, d), []).append((b, e))
                used_min[(mber, d)] = used_min.get((mber, d), 0) + dur

            sn = len(resves) + 1
            nmpr = rnd.randint(max(1, cap // 4), cap)
            purps = rnd.choice(PURPOSES[ty])
            # 시각(SYSDATE 기준 일수): 신청 → 처리 → (취소·이용완료)
            # 규칙: 신청일 < 이용일, 신청 시점에 이용일이 30일 이내, 취소는 이용일 전날까지
            if d < 0:
                regist = d - rnd.uniform(2, 14)
                decide = regist + rnd.uniform(0.1, 0.9)
                cancel_by = d - 1
            else:
                low = 1.0 if d == 0 else 0.05
                regist = -rnd.uniform(low, max(low + 0.5, min(12, 29 - d)))
                decide = regist + (0 - regist) * rnd.uniform(0.2, 0.9)
                cancel_by = -0.05
            rsn = rnd.choice(RETURN_RSN) if sttus == '03' else None
            h = [(None, '01', None, mber, regist)]
            if sttus == '02':
                h.append(('01', '02', None, 'admin', decide))
            elif sttus == '03':
                h.append(('01', '03', rsn, 'admin', decide))
            elif sttus == '05':
                h.append(('01', '02', None, 'admin', decide))
                h.append(('02', '05', None, 'system', d + e / 1440.0 - rnd.uniform(0, 0.01)))
            elif sttus == '04':
                if rnd.random() < 0.5:
                    h.append(('01', '02', None, 'admin', decide))
                    bound = cancel_by if cancel_by > decide else decide / 2  # 처리 직후라도 처리보다 늦게
                    h.append(('02', '04', None, mber, decide + (bound - decide) * rnd.uniform(0.3, 0.9)))
                else:
                    h.append(('01', '04', None, mber, decide))
            last = h[-1]
            upd_id = last[3] if len(h) > 1 else None
            upd_dt = last[4] if len(h) > 1 else None
            resves.append((sn, fi, mber, d, hhmi(b), hhmi(e), nmpr, purps, sttus, rsn, regist, upd_id, upd_dt))
            for bfr, aft, crsn, who, at in h:
                hists.append((len(hists) + 1, sn, bfr, aft, crsn, who, at))

# 데모 계정이 화면에서 바로 확인할 수 있는 예약 (규칙 안에서만 추가)
for mber, fi, d, b, e, st in [('citizen1', 1, 2, 600, 720, '02'), ('citizen1', 5, 5, 840, 900, '01'),
                              ('citizen2', 10, 7, 840, 960, '01'), ('citizen2', 6, 0, 1140, 1200, '02')]:
    if free(fi, d, b, e) and used_min.get((mber, d), 0) + (e - b) <= LIMIT_MIN:
        occupied.setdefault((fi, d), []).append((b, e))
        used_min[(mber, d)] = used_min.get((mber, d), 0) + (e - b)
        sn = len(resves) + 1
        regist = -rnd.uniform(1, 3)
        decide = -rnd.uniform(0.1, 0.9)
        ty = FCLTIES[fi - 1][0]
        resves.append((sn, fi, mber, d, hhmi(b), hhmi(e), 8, rnd.choice(PURPOSES[ty]), st, None, regist,
                       'admin' if st == '02' else None, decide if st == '02' else None))
        hists.append((len(hists) + 1, sn, None, '01', None, mber, regist))
        if st == '02':
            hists.append((len(hists) + 1, sn, '01', '02', None, 'admin', decide))

# ---------- SQL 출력 ----------
L = ['-- 자동 생성 파일: gen_data.py 로 만든다 (직접 고치지 말 것)',
     '-- 데모 계정: admin / Admin1234!, citizen1·citizen2·user03~user30 / Citizen1234! (로컬 데모 전용)',
     f'-- 회원 {len(members)}명, 시설 {len(FCLTIES)}곳, 공지 {len(NOTICES)}건, 예약 {len(resves)}건, 이력 {len(hists)}건',
     'SET DEFINE OFF', '']
for cid, rows in (('FCLTY_TY', ['체육관', '회의실', '강당', '운동장']),
                  ('RESVE_STTUS', ['신청', '승인', '반려', '취소', '이용완료'])):
    for i, nm in enumerate(rows, start=1):
        L.append(f"INSERT INTO TB_CMMN_CODE (CODE_ID, CODE, CODE_NM, SORT_ORDR) VALUES ('{cid}', '{i:02d}', '{nm}', {i});")
for m in members:
    L.append(f"INSERT INTO TB_MBER (MBER_ID, PASSWORD, MBER_NM, AUTHOR_CD, REGIST_DT) VALUES ({q(m[0])}, {q(m[1])}, {q(m[2])}, {q(m[3])}, {ts(-rnd.uniform(40, 200))});")
for i, f in enumerate(FCLTIES, start=1):
    ty, nm, adr, cap, ob, oe, unit, dc, use = f
    L.append("INSERT INTO TB_FCLTY (FCLTY_SN, FCLTY_TY_CD, FCLTY_NM, ADRES, ACMD_NMPR, OPER_BEGIN_TM, OPER_END_TM, RESVE_UNIT_MIN, FCLTY_DC, USE_YN, REGISTER_ID, REGIST_DT) "
             f"VALUES ({i}, {q(ty)}, {q(nm)}, {q(adr)}, {cap}, {q(ob)}, {q(oe)}, {unit}, {q(dc)}, {q(use)}, 'admin', {ts(-rnd.uniform(60, 300))});")
for i, (sj, cn) in enumerate(NOTICES, start=1):
    cn_sql = q(cn).replace('\n', "' || CHR(10) || '")
    L.append(f"INSERT INTO TB_NOTICE (NTT_SN, NTT_SJ, NTT_CN, RDCNT, REGISTER_ID, REGIST_DT) VALUES ({i}, {q(sj)}, {cn_sql}, {rnd.randint(3, 240)}, 'admin', {ts(-(len(NOTICES) - i) * 4 - rnd.uniform(0, 3))});")
for sn, fi, mber, d, b, e, nmpr, purps, st, rsn, regist, upd_id, upd_dt in resves:
    L.append("INSERT INTO TB_RESVE (RESVE_SN, FCLTY_SN, MBER_ID, RESVE_DE, BEGIN_TM, END_TM, USE_NMPR, USE_PURPS, RESVE_STTUS_CD, RETURN_RSN, REGIST_DT, UPDUSR_ID, UPDT_DT) "
             f"VALUES ({sn}, {fi}, {q(mber)}, {de(d)}, {q(b)}, {q(e)}, {nmpr}, {q(purps)}, {q(st)}, {q(rsn)}, {ts(regist)}, {q(upd_id)}, {ts(upd_dt) if upd_dt is not None else 'NULL'});")
for hsn, sn, bfr, aft, crsn, who, at in hists:
    L.append("INSERT INTO TB_RESVE_HIST (HIST_SN, RESVE_SN, BFR_STTUS_CD, AFT_STTUS_CD, CHANGE_RSN, CHANGER_ID, CHANGE_DT) "
             f"VALUES ({hsn}, {sn}, {q(bfr)}, {q(aft)}, {q(crsn)}, {q(who)}, {ts(at)});")
# 직접 번호를 넣었으므로 시퀀스를 다음 번호부터 시작하게 다시 만든다
for seq, nxt in (('SEQ_FCLTY', len(FCLTIES) + 1), ('SEQ_NOTICE', len(NOTICES) + 1),
                 ('SEQ_RESVE', len(resves) + 1), ('SEQ_RESVE_HIST', len(hists) + 1)):
    L.append(f'DROP SEQUENCE {seq};')
    L.append(f'CREATE SEQUENCE {seq} START WITH {nxt} INCREMENT BY 1;')
L.append('COMMIT;')

with open(OUT, 'w', encoding='utf-8', newline='\n') as fp:
    fp.write('\n'.join(L) + '\n')
print(f'{OUT}: 회원 {len(members)}, 시설 {len(FCLTIES)}, 공지 {len(NOTICES)}, 예약 {len(resves)}, 이력 {len(hists)}')
