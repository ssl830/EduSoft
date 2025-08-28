pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/course/create"
LOGIN_URL = "http://localhost:3000/login"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    # 登录后如果跳转到 login?redirect=xxx，需再次跳转目标页
    if goto_url:
        if page.url.startswith("http://localhost:3000/login?redirect="):
            page.goto(goto_url)
        else:
            page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_course_create_page_render(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    expect(page.locator("h1.page-title")).to_have_text("创建课程")
    expect(page.locator("input#name")).to_be_visible()
    expect(page.locator("input#outline").first).to_be_visible()
    expect(page.locator("input#objective")).to_be_visible()
    expect(page.locator("input#assessment")).to_be_visible()
    context.close()
    browser.close()

def test_course_create_validation(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    # 课程名为空
    page.locator("input#name").fill("")
    page.locator("input#outline").first.fill("大纲")
    page.locator("input#objective").fill("目标")
    page.locator("input#assessment").fill("考核")
    page.locator("button.btn-primary").click()
    expect(page.locator(".error-message")).to_contain_text("填写课程名称")
    context.close()
    browser.close()

def test_course_create_success(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    page.locator("input#name").fill("自动化测试课程")
    page.locator("input#outline").first.fill("测试大纲")
    # 课程代码输入
    page.locator("input[placeholder='输入课程代码']").fill("TEST101")
    page.locator("input#objective").fill("测试目标")
    page.locator("input#assessment").fill("测试考核")
    page.locator("button.btn-primary").click()
    # 跳转首页
    expect(page).to_have_url("http://localhost:3000/")
    context.close()
    browser.close()
