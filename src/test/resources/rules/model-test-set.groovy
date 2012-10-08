rule('R1') {
    _if_ = [{ X == 0 }, { T == 3 }]
    _then_ = { RESULT = 7 }
    _reason_ = 'R1'
}
rule('R2') {
    _if_ = [{ X == 0 }, { T > 5 }]
    _then_ = { RESULT = 3 }
    _reason_ = 'R2'
}