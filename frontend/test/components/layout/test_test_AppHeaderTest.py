pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import Page, expect

LOGIN_URL = "http://localhost:3000/login"
HOME_URL = "http://localhost:3000/"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_context(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if goto_url:
        page.goto(goto_url)
        expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    return browser, context

def test_header_login_logout(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, HOME_URL)
    page = context.new_page()
    page.goto(HOME_URL)
    expect(page.locator(".app-header")).to_be_visible()
    expect(page.locator(".user-greeting")).to_contain_text("你好")
    page.locator(".logout-btn").click()
    expect(page.locator(".btn-auth.login")).to_be_visible()
    context.close()
    browser.close()

def test_header_nav_and_help_feedback(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, HOME_URL)
    page = context.new_page()
    page.goto(HOME_URL)
    # 检查导航栏
    expect(page.locator(".main-nav")).to_be_visible()
    expect(page.locator(".nav-link:has-text('首页')")).to_be_visible()
    expect(page.locator(".nav-link:has-text('题库')")).to_be_visible()
    # 帮助中心弹窗
    page.locator(".nav-link:has-text('帮助中心')").click()
    expect(page.locator(".modal-content")).to_contain_text("帮助中心")
    page.locator(".close-btn").click()
    expect(page.locator(".modal-content")).not_to_be_visible(timeout=3000)
    # 意见反馈弹窗
    page.locator(".nav-link:has-text('意见反馈')").click()
    expect(page.locator(".modal-content")).to_contain_text("意见反馈")
    page.locator(".close-btn").click()
    expect(page.locator(".modal-content")).not_to_be_visible(timeout=3000)
    context.close()
    browser.close()

def test_header_student_view(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, HOME_URL)
    page = context.new_page()
    page.goto(HOME_URL)
    expect(page.locator(".app-header")).to_be_visible()
    expect(page.locator(".user-greeting")).to_contain_text("你好")
    # 学生没有题库入口
    expect(page.locator(".nav-link:has-text('题库')")).not_to_be_visible()
    context.close()
    browser.close()

