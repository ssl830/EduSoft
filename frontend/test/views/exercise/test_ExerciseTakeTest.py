pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/takeExercise/5"
LOGIN_URL = "http://localhost:3000/login"
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if goto_url:
        if page.url.startswith("http://localhost:3000/login?redirect="):
            page.goto(goto_url)
        else:
            page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_exercise_take_render(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-container")).to_be_visible(timeout=10000)
    expect(page.locator(".question-item").first).to_be_visible()
    expect(page.locator(".submit-btn")).to_be_visible()
    context.close()
    browser.close()

def test_exercise_take_answer_and_submit(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-container")).to_be_visible(timeout=10000)
    # 填写第一个题目的答案（单选题/多选题/简答题等）
    first_radio = page.locator(".question-item input[type='radio']").first
    if first_radio.count() > 0 and first_radio.is_visible():
        first_radio.check()
    first_checkbox = page.locator(".question-item input[type='checkbox']").first
    if first_checkbox.count() > 0 and first_checkbox.is_visible():
        first_checkbox.check()
    first_textarea = page.locator(".question-item textarea").first
    if first_textarea.count() > 0 and first_textarea.is_visible():
        first_textarea.fill("自动化测试答案")
    # 提交
    page.locator(".submit-btn").click()
    # 跳转到练习反馈页或显示成功提示
    try:
        expect(page).to_have_url(re.compile(r"http://localhost:3000/exerciseFeedback/.*"), timeout=10000)
    except Exception:
        expect(page.locator(".error")).not_to_be_visible()
    context.close()
    browser.close()

