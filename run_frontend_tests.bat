@echo on
python -m playwright install
pytest frontend\test --maxfail=1 --disable-warnings -v -s
