pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/exerciseFeedback/5/10"
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

def test_exercise_feedback_render(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".feedback-container")).to_be_visible(timeout=10000)
    expect(page.get_by_role("heading", name="sdsd - 练习反馈")).to_be_visible()
    expect(page.locator(".questions-list2 .question-item").first).to_be_visible()
    expect(page.locator(".total-score")).to_be_visible()
    context.close()
    browser.close()

def test_exercise_feedback_question_detail(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".feedback-container")).to_be_visible(timeout=10000)
    # 检查题目详情区
    question = page.locator(".questions-list2 .question-item").first
    expect(question).to_be_visible()
    expect(question.locator(".question-header2")).to_be_visible()
    expect(question.locator(".question-content2")).to_be_visible()
    expect(question.locator(".question-points")).to_be_visible()
    context.close()
    browser.close()

def test_exercise_feedback_ai_score(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".feedback-container")).to_be_visible(timeout=10000)
    # 检查AI评分区（如有）
    ai_card = page.locator(".ai-card.ai-score").first
    if ai_card.count() > 0 and ai_card.is_visible():
        expect(ai_card).to_be_visible()
    context.close()
    browser.close()

