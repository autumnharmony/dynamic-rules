rule('R1') {
    _if_ = [{ O == WORKSHOP }, {V > 10}]
    _then_ = { RESULT = [NKPK] }
    _reason_ = 'R1'
}