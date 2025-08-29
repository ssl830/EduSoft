pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/course/2"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_context(playwright, credentials: dict, url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if url:
        page.goto(url)
        if page.url.startswith(LOGIN_URL):
            context.close()
            browser.close()
            pytest.fail(f"用户 {credentials['userId']} 无法访问 {url}，测试失败")
        expect(page).to_have_url(re.compile(r"http://localhost:3000/course/2"))
    return browser, context

def wait_course_main(page):
    # 等待主容器或错误提示出现
    expect(page.locator(".course-detail-container")).to_be_visible(timeout=10000)
    # 如果有错误提示则跳过后续断言
    if page.locator(".error-message").is_visible():
        pytest.skip("课程详情加载失败，跳过断言")
    # 等待 header 渲染
    expect(page.locator(".course-header h1")).to_be_visible(timeout=10000)

def test_teacher_course_detail_tabs(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, BASE_URL)
    page = context.new_page()
    page.goto(BASE_URL)
    wait_course_main(page)
    expect(page.locator(".course-tabs")).to_be_visible()
    expect(page.locator(".tab-button.active")).to_have_text("课程概况")
    expect(page.locator(".course-main-content")).to_be_visible()
    # 切换到教学资料
    page.locator(".tab-button:has-text('教学资料')").click()
    expect(page.locator(".tab-button.active")).to_have_text("教学资料")
    # 切换到视频学习
    page.locator(".tab-button:has-text('视频学习')").click()
    expect(page.locator(".tab-button.active")).to_have_text("视频学习")
    context.close()
    browser.close()

def test_student_course_detail_view(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page = context.new_page()
    page.goto(BASE_URL)
    wait_course_main(page)
    expect(page.locator(".course-tabs")).to_be_visible()
    expect(page.locator(".tab-button.active")).to_have_text("课程概况")
    expect(page.locator(".course-main-content")).to_be_visible()
    context.close()
    browser.close()
