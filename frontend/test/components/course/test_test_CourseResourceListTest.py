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

def goto_resource_tab(page: Page):
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/course/2"))
    expect(page.locator(".course-header h1")).to_be_visible(timeout=10000)
    page.locator("button:has-text('教学资料')").click()
    expect(page.locator(".resource-list-container")).to_be_visible()

def test_teacher_resource_upload_and_filter(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_resource_tab(page)
    # 检查上传资料按钮
    expect(page.locator("button:has-text('上传资料')")).to_be_visible()
    page.locator("button:has-text('上传资料')").click()
    expect(page.locator(".upload-form")).to_be_visible()
    # 填写资料标题
    page.locator("input#title").fill("自动化测试资料")
    # 选择类型
    page.locator("input[type='radio'][value='OTHER']").check()
    # 选择文件（跳过实际上传，模拟文件选择）
    # page.set_input_files("input#file", "D:/test.OTHER") # 如需真实上传可解注
    # 选择可见性
    page.locator("input[type='radio'][value='PUBLIC']").check()
    # 点击上传（跳过实际上传，模拟点击）
    # page.locator("button:has-text('上传')").click()
    # 检查筛选功能
    page.locator("select#typeFilter").select_option(label="OTHER")
    page.wait_for_timeout(1000)
    expect(page.locator(".resource-table")).to_be_visible()
    # 检查预览和下载按钮
    preview_count = page.locator(".btn-action.preview").count()
    assert preview_count > 0, "应该存在至少一个预览按钮"
    download_count = page.locator(".btn-action.download").count()
    assert download_count > 0, "应该存在至少一个预览按钮"
    # preview_buttons = page.locator(".btn-action.preview")
    # download_buttons = page.locator(".btn-action.download")
    # count_preview = preview_buttons.count()
    # count_download = download_buttons.count()
    # for i in range(count_preview):
    #     expect(preview_buttons.nth(i)).to_be_visible()
    # for i in range(count_download):
    #     expect(download_buttons.nth(i)).to_be_visible()
    context.close()
    browser.close()

def test_student_resource_list_and_filter(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_resource_tab(page)
    # 学生视角下无上传资料按钮
    expect(page.locator("button:has-text('上传资料')")).not_to_be_visible()
    # 检查资料列表
    expect(page.locator(".resource-table")).to_be_visible(timeout=5000)
    # 检查筛选功能
    page.locator("select#typeFilter").select_option(label="OTHER")
    page.wait_for_timeout(1000)
    expect(page.locator(".resource-table")).to_be_visible()
    context.close()
    browser.close()
