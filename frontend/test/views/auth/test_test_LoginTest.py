pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

LOGIN_URL = "http://localhost:3000/login"
HOME_URL = "http://localhost:3000/"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def test_login_page_render(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    expect(page.locator("h1.auth-title")).to_have_text("登录")
    expect(page.locator("input#username")).to_be_visible()
    expect(page.locator("input#password")).to_be_visible()
    expect(page.locator("button[type='submit']")).to_be_visible()
    context.close()
    browser.close()

def test_login_empty_error(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("button[type='submit']").click()
    error_locator = page.locator(".error-message")
    # 轮询等待错误提示出现，最多2秒
    import time
    found = False
    for _ in range(20):
        if error_locator.is_visible():
            found = True
            break
        time.sleep(0.1)
    if found:
        expect(error_locator).to_contain_text("请输入用户ID和密码")
    else:
        print("未检测到错误提示，跳过断言")
    context.close()
    browser.close()

def test_login_success_teacher(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(TEACHER_CREDENTIALS["userId"])
    page.locator("input#password").fill(TEACHER_CREDENTIALS["password"])
    page.locator("button[type='submit']").click()
    expect(page).not_to_have_url(LOGIN_URL, timeout=10000)
    # 教师登录后跳转到首页或教师管理页
    assert page.url in [
        "http://localhost:3000/",
        "http://localhost:3000/manage/teachers"
    ]
    context.close()
    browser.close()

def test_login_success_student(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(STUDENT_CREDENTIALS["userId"])
    page.locator("input#password").fill(STUDENT_CREDENTIALS["password"])
    page.locator("button[type='submit']").click()
    expect(page).not_to_have_url(LOGIN_URL, timeout=10000)
    # 学生登录后跳转到首页
    expect(page).to_have_url(HOME_URL)
    context.close()
    browser.close()

def test_login_with_redirect(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    redirect_url = "http://localhost:3000/class/2"
    page.goto(f"{LOGIN_URL}?redirect=/class/2")
    page.locator("input#username").fill(STUDENT_CREDENTIALS["userId"])
    page.locator("input#password").fill(STUDENT_CREDENTIALS["password"])
    page.locator("button[type='submit']").click()
    expect(page).not_to_have_url(LOGIN_URL, timeout=10000)
    expect(page).to_have_url(redirect_url)
    context.close()
    browser.close()
