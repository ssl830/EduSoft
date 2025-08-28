pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/ai-selftest"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, target_url: str):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    # 登录后再跳转目标页
    page.goto(target_url)
    return browser, context, page

def test_student_ai_selftest_render(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/ai-selftest"))
    expect(page.locator("h1.main-title")).to_have_text("AI 自测练习生成")
    # 分别检查每个表单区域
    expect(page.locator(".form-section:has-text('练习要求')")).to_be_visible()
    expect(page.locator(".form-section:has-text('知识点偏好')")).to_be_visible()
    expect(page.locator(".form-section:has-text('生成模式')")).to_be_visible()
    expect(page.locator(".button-group .btn.primary")).to_be_visible()
    # 切换到选题模式
    page.locator("select.mode-select").select_option("selected")
    expect(page.locator(".question-list")).to_be_visible(timeout=5000)
    expect(page.locator(".tab-btn:has-text('错题库')")).to_be_visible()
    expect(page.locator(".tab-btn:has-text('收藏题库')")).to_be_visible()
    context.close()
    browser.close()

def test_teacher_ai_selftest_forbidden(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    # 教师访问会被重定向��首页
    expect(page).to_have_url(re.compile(r"http://localhost:3000/?"))
    context.close()
    browser.close()

def test_admin_ai_selftest_forbidden(playwright):
    browser, context, page = login_and_get_page(playwright, ADMIN_CREDENTIALS, BASE_URL)
    # 管理员访问会被重定向到首页
    expect(page).to_have_url(re.compile(r"http://localhost:3000/?"))
    context.close()
    browser.close()
