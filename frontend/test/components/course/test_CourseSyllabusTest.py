pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import Page, expect

BASE_URL = "http://localhost:3000/course/2"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_context(playwright, credentials: dict, course_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if course_url:
        page.goto(course_url)
        if page.url.startswith(LOGIN_URL):
            context.close()
            browser.close()
            pytest.fail(f"用户 {credentials['userId']} 无法访问 {course_url}，测试失败")
        expect(page).to_have_url(re.compile(r"http://localhost:3000/course/2"))
    return browser, context

def goto_syllabus_tab(page: Page):
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/course/2"))
    expect(page.locator(".course-header h1")).to_be_visible(timeout=10000)
    page.locator("button:has-text('课程概况')").click()
    expect(page.locator(".course-syllabus")).to_be_visible()

def test_teacher_syllabus_edit_and_section(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_syllabus_tab(page)
    # 检查编辑按钮
    expect(page.locator("button.btn-edit:has-text('编辑')")).to_be_visible()
    # 检查新增章节按钮
    expect(page.locator("button.btn-edit:has-text('新增章节')")).to_be_visible()
    page.locator("button.btn-edit:has-text('新增章节')").click()
    expect(page.locator(".upload-form")).to_be_visible()
    # 填写章节信息
    page.locator("input[placeholder='章节号']").fill("1")
    page.locator("input[placeholder='章节1标题']").fill("自动化测试章节")
    page.locator("button.btn-primary:has-text('上传')").click()
    page.wait_for_timeout(1000)
    # 检查章节表格
    expect(page.locator(".resource-table")).to_be_visible()
    # 删除章节（如果有删除按钮）
    if page.locator(".btn-action.preview[title='删除']").count() > 0:
        page.locator(".btn-action.preview[title='删除']").first.click()
        page.wait_for_timeout(500)
    context.close()
    browser.close()

def test_student_syllabus_readonly(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_syllabus_tab(page)
    # 学生视角下无编辑和新增章节按钮
    expect(page.locator("button.btn-edit:has-text('编辑')")).not_to_be_visible()
    expect(page.locator("button.btn-edit:has-text('新增章节')")).not_to_be_visible()
    # 检查课程信息展示
    expect(page.locator(".course-syllabus")).to_be_visible()
    context.close()
    browser.close()

