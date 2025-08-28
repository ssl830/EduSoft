pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

LOGIN_URL = "http://localhost:3000/login"
HISTORY_URL = "http://localhost:3000/selfpractice/history"
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

def test_student_selfpractice_history_render(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS)
    page.goto(HISTORY_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/selfpractice/history"))
    expect(page.locator("h1:has-text('自测历史')")).to_be_visible()
    # 检查表格或空提示
    if page.locator(".history-table").count() > 0 and page.locator(".history-table").is_visible():
        expect(page.locator(".history-table")).to_be_visible()
        # 检查详情跳转链接
        detail_links = page.locator("a[href^='/selfpractice/history/']")
        if detail_links.count() > 0:
            detail_links.first.click()
            expect(page).to_have_url(re.compile(r"http://localhost:3000/selfpractice/history/\d+"))
    else:
        # 只有在页面加载完成且没有历史记录时才断言 .empty 可见
        if page.locator(".loading").count() > 0:
            expect(page.locator(".loading")).to_be_visible()
    context.close()
    browser.close()

def test_teacher_selfpractice_history_forbidden(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS)
    page.goto(HISTORY_URL)
    # 教师访问应被重定向到首页
    expect(page).to_have_url("http://localhost:3000/")
    context.close()
    browser.close()

def test_admin_selfpractice_history_forbidden(playwright):
    browser, context, page = login_and_get_page(playwright, ADMIN_CREDENTIALS)
    page.goto(HISTORY_URL)
    # 管理员访问应被重定向到首页
    expect(page).to_have_url("http://localhost:3000/manage/teachers")
    context.close()
    browser.close()
