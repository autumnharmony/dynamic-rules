rule('R2') {
    _if_ = [{ O == WORKSHOP }, {V > 60}, {V<=200}]
    _then_ = { RESULT = [NKPK] }
    _reason_ = 'Выбрано объект обслуживания Цех и скорость подъема - от 60 до 200'
}
rule('R3') {
    _if_ = [{ O == WORKSHOP }, {V >= 1}, {V <= 60}, {H >= 1}, {H < 3}]
    _then_ = { RESULT = [NKPK] }
    _reason_ = 'Выбран объект обслуживания Цех, скорость подъема - от 1 до 60 и высота подъема от 1 до 3'
}
rule('R4') {
    _if_ = [{ O == WORKSHOP }, {V >= 1}, {V <= 60}, {H > 10}, {H <= 50}]
    _then_ = { RESULT = [MK] }
    _reason_ = 'Выбран объект обслуживания Цех, скорость подъема - от 1 до 60 и высота подъема от 10 до 50'
}
rule('R5') {
    _if_ = [{ O == WORKSHOP }, {V >= 1}, {V <= 60}, {H >= 3}, {H <= 10}, {W >= 0.1}, {W < 1}]
    _then_ = { RESULT = [NKPK] }
    _reason_ = 'Выбран объект обслуживания Цех,\n скорость подъема - от 1 до 60,\n высота подъема от 3 до 10\n и грузоподъемность от 0.1 до 1'
}
rule('R6') {
    _if_ = [{ O == WORKSHOP }, {V >= 1}, {V <= 60}, {H >= 3}, {H <= 10}, {W >= 1}, {W < 10}]
    _then_ = { RESULT = [NKPK, MK] }
    _reason_ = 'Выбран объект обслуживания Цех,\n скорость подъема - от 1 до 60,\n высота подъема от 3 до 10\n и грузоподъемность от 1 до 10'
}
rule('R7') {
    _if_ = [{ O == WORKSHOP }, {V >= 1}, {V <= 60}, {H >= 3}, {H <= 10}, {W > 10}, {W <= 600}]
    _then_ = { RESULT = [MK] }
    _reason_ = 'Выбран объект обслуживания Цех,\n скорость подъема - от 1 до 60,\n высота подъема от 3 до 10\n и грузоподъемность от 10 до 600'
}