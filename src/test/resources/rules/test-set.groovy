rule('R1') {
    _if_ = [{ C == 1 }]
    _then_ = { RESULT = [A] }
    _reason_ = 'R1'
}
rule('R2') {
    _if_ = [{ T == 2}]
    _then_ = { C = 1 }
    _reason_ = 'R2'
}
rule('R3') {
    _if_ = [{T == 3}, { X >= 0 }, { X < 50 }]
    _then_ = { RESULT = [A] }
    _reason_ = 'R3'
}
rule('R4') {
    _if_ = [{T == 3}, { X >= 50 }, {X <= 100 }]
    _then_ = { RESULT = [A, B] }
    _reason_ = 'R4'
}
rule('R5') {
    _if_ = [{T == 3}, { X > 100 }, {X <= 120}]
    _then_ = { RESULT = [B] }
    _reason_ = 'R5'
}