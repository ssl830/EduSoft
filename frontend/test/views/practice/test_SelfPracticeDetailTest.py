pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

LOGIN_URL = "http://localhost:3000/login"
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    return browser, context, page

def get_first_self_practice_pid(page):
    # 跳转到自测历史页面，获取第一个自测记录的 practiceId
    page.goto("http://localhost:3000/selfpractice/history")
    expect(page).to_have_url(re.compile(r"http://localhost:3000/selfpractice/history"))
    # 获取第一个详情链接
    detail_links = page.locator("a[href^='/selfpractice/history/']")
    if detail_links.count() == 0:
        pytest.skip("当前学生没有自测历史数据，跳过测试")
    href = detail_links.first.get_attribute("href")
    pid = href.split("/")[-1]
    return pid

def test_student_selfpractice_detail(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS)
    pid = get_first_self_practice_pid(page)
    detail_url = f"http://localhost:3000/selfpractice/history/{pid}"
    page.goto(detail_url)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/selfpractice/history/"))
    expect(page.locator("h1.title")).to_have_text("自测详情")
    # 题目列表至少有一题
    question_items = page.locator(".question-item")
    assert question_items.count() > 0
    # 检查题干、选项、答案、得分区域
    expect(question_items.first.locator(".question-title")).to_be_visible()
    expect(question_items.first.locator(".option-list")).to_be_visible()
    expect(question_items.first.locator(".answer-row")).to_be_visible()
    expect(question_items.first.locator(".score")).to_be_visible()
    context.close()
    browser.close()

def test_teacher_selfpractice_detail_forbidden(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS)
    # 教师访问自测详情应被重定向到首页
    page.goto("http://localhost:3000/selfpractice/history/1")
    expect(page).to_have_url("http://localhost:3000/")
    context.close()
    browser.close()

def test_admin_selfpractice_detail_forbidden(playwright):
    browser, context, page = login_and_get_page(playwright, ADMIN_CREDENTIALS)
    # 管理员访问自测详情应被重定向到首页
    page.goto("http://localhost:3000/selfpractice/history/1")
    expect(page).to_have_url("http://localhost:3000/manage/teachers")
    context.close()
    browser.close()

